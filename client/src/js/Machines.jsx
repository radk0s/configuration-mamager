const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const {Grid, Row, Col, Table, Button} = require('react-bootstrap');
const ModalTrigger = require('react-bootstrap').ModalTrigger;
const async = require("async");
const _ = require('underscore');
const moment = require('moment');
const CreateInstance = require('./CreateInstance.jsx');

let Machines = React.createClass({
    getInitialState() {
        return {
            DO: [],
            AWS: [],
            confs: []
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
                if (typeof res != "undefined" || res != null) {
                  callback(null, res.body.droplets);
                }
              });
          },
          AWS: function(callback){
            request
              .get('/instances/aws')
              .set('authToken', auth.getToken())
              .set('Accept', 'application/json')
              .end((err, res) => {
                if (typeof res != "undefined" || res != null) {
                  callback(null, _.values(res.body));
                }
              });
          }
        },
        function(err, results) {
          component.setState({
            DO: results.DO,
            AWS: results.AWS
          }, () => { console.log("results fetched")});
        });
    },
    render() {
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
                  <Button bsSize='large' onClick={() => runMachine('do', item.id)}>Run</Button>
                  <Button bsSize='large' onClick={() => stopMachine('do', item.id)}>Stop</Button>
                  <Button bsSize='large' onClick={() => deleteMachine('do', item.id)}>Terminate</Button>
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
              <Button bsSize='large' onClick={() => runMachine('aws', item.instanceId)}>Run</Button>
              <Button bsSize='large' onClick={() => stopMachine('aws', item.instanceId)}>Stop</Button>
              <Button bsSize='large' onClick={() => deleteMachine('aws', item.instanceId)}>Terminate</Button>
            </td>
          </tr>
        )
      });

        return(
          <div>
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
          </div>);
    }
});


module.exports = Machines;
