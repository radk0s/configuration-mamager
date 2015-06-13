const React = require('react/addons');
const auth = require("./Auth.js");
const request = require('superagent');
const {Grid, Row, Col, Table, Button, Input} = require('react-bootstrap');
const ModalTrigger = require('react-bootstrap').ModalTrigger;
const async = require("async");
const _ = require('underscore');
const moment = require('moment');
const CreateInstance = require('./CreateInstance.jsx');
const TerminalModal = require('./TerminalModal.jsx');
const Loader = require('react-loader');

let Machines = React.createClass({
  mixins: [React.addons.LinkedStateMixin],
  getInitialState() {
        return {
            DO: [],
            AWS: [],
            confs: [],
            loaded: false
        }
    },
    componentDidMount() {
      let component = this;
      request
        .get('/configuration')
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .end((err, res) => {
          component.setState({
            confs: res.body
          });
        });
      this.listMachines();
      this.setState({
        interval: setInterval(this.listMachines,2000)
      });
    },
    componentWillUnmount() {
      clearInterval(this.interval);
    },
    listMachines() {
      let component = this;

      async.parallel({
          DO: function(callback){
            request
              .get('/instances/do')
              .set('authToken', auth.getToken())
              .set('Accept', 'application/json')
              .end((err, res) => {
                if ( !_.isEmpty(res) && !_.isEmpty(res.body) && !_.isEmpty(res.body.droplets) ) {
                  callback(null, res.body.droplets);
                } else {
                  callback(null, []);
                }
              });
          },
          AWS: function(callback){
            request
              .get('/instances/aws')
              .set('authToken', auth.getToken())
              .set('Accept', 'application/json')
              .end((err, res) => {
                if ( !_.isEmpty(res) && !_.isEmpty(res.body) ) {
                  callback(null, _.values(res.body));
                } else {
                  callback(null, []);
                }
              });
          }
        },
        function(err, results) {
          component.setState({
            DO: results.DO,
            AWS: results.AWS,
            loaded: true
          }, () => { console.log("results fetched")});
        });
    },
    render() {
      let component = this;
      function deleteMachine(provider, id) {
        request
          .del(`/instances/${provider}`)
          .set('authToken', auth.getToken())
          .set('Accept', 'application/json')
          .send({ instanceId: id})
          .end((err, res) => {
            console.log(err);
            console.log(res);
          });
      }
      function stopMachine(provider, id) {
        request
          .post(`/instances/${provider}/stop`)
          .set('authToken', auth.getToken())
          .set('Accept', 'application/json')
          .send({ instanceId: id})
          .end((err, res) => {
            console.log(err);
            console.log(res);
          });
      }
      function runMachine(provider, id) {
        request
          .post(`/instances/${provider}/run`)
          .set('authToken', auth.getToken())
          .set('Accept', 'application/json')
          .send({ instanceId: id})
          .end((err, res) => {
            console.log(err);
            console.log(res);
          });
      }

        var listDOItems = this.state.DO.map(function(item) {
            return (
              <tr>
                <td>DO</td>
                <td>{item.name}</td>
                <td>{item.image.distribution}</td>
                <td>{item.networks.v4[0].ip_address}</td>
                <td>{item.status}</td>
                <td>{item.memory}</td>
                <td>{moment(item.created_at).format("dddd, MMMM Do YYYY, h:mm:ss a")}</td>
                <td>
                  <Button className={'floating'} bsSize='small' onClick={() => runMachine('do', item.id)}>Run</Button>
                  <Button className={'floating'} bsSize='small' onClick={() => stopMachine('do', item.id)}>Stop</Button>
                  <Button className={'floating'} bsSize='small' onClick={() => deleteMachine('do', item.id)}>Terminate</Button>
                  <Input className={'floating short-input'} type='text' placeholder='username' valueLink={component.linkState('usernameDo')}/>
                  <ModalTrigger  className={'floating'} modal={<TerminalModal host={item.networks.v4[0].ip_address} username={component.state.usernameDo}/>}>
                    <Button className={'floating'} bsStyle='primary' bsSize='small'>SSH</Button>
                  </ModalTrigger>

                </td>
              </tr>
              )
        });

      var listAWSItems = this.state.AWS.map(function(item) {
        let item = item[0];
        return (
          <tr>
            <td>AWS</td>
            <td>{item.instanceId}</td>
            <td>{item.imageId}</td>
            <td>{item.publicIpAddress}</td>
            <td>{item.state.name}</td>
            <td>{item.instanceType}</td>
            <td>{moment(item.launchTime).format("dddd, MMMM Do YYYY, h:mm:ss a")}</td>
            <td>
              <Button className={'floating'} bsSize='small' onClick={() => runMachine('aws', item.instanceId)}>Run</Button>
              <Button className={'floating'} bsSize='small' onClick={() => stopMachine('aws', item.instanceId)}>Stop</Button>
              <Button className={'floating'} bsSize='small' onClick={() => deleteMachine('aws', item.instanceId)}>Terminate</Button>
              <Input className={'floating short-input'} type='text' placeholder='username' valueLink={component.linkState('usernameAws')}/>
              <ModalTrigger  className={'floating'} modal={<TerminalModal host={item.publicIpAddress} username={component.state.usernameAws}/>}>
                <Button className={'floating'} bsStyle='primary' bsSize='small'>SSH</Button>
              </ModalTrigger>
            </td>
          </tr>
        )
      });

        return(
          <div>
            <Loader loaded={this.state.loaded}>
            <Table responsive>
              <thead>
                <tr>
                  <th>Provider</th>
                  <th>Name/Id</th>
                  <th>Image</th>
                  <th>IPv4</th>
                  <th>Status</th>
                  <th>Size</th>
                  <th>Last Run At</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {listDOItems}
                {listAWSItems}
              </tbody>
            </Table>
            <ModalTrigger modal={<CreateInstance title={`Create Instance`} confs={_.values(this.state.confs)}/>} >
              <Button bsSize='large'>Create instances</Button>
            </ModalTrigger>
            </Loader>
          </div>);
    }
});


module.exports = Machines;
