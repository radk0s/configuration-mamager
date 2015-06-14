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
const AWSModal = require('./AWSModal.jsx');
const Loader = require('react-loader');
const FetchDataMixin = require('./FetchDataMixin.js');

let Machines = React.createClass({
  mixins: [React.addons.LinkedStateMixin, FetchDataMixin],
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
      this.fetch(`/configuration`,'get', {}, 'confs');
      this.listMachines();
      this.setState({
        interval: setInterval(this.listMachines,2000)
      });
    },
    componentWillUnmount() {
      clearInterval(this.state.interval);
    },
    listMachines() {
      let component = this;

      function listSnapshots() {
        if(component.state.DOLoaded && component.state.AWSLoaded) {
          if(component.state.dropletProvider == "") return;
          component.listSnapshots()
        }
      }

      this.fetch('/instances/do','get', {}, 'DO', (data) => {
        return data.droplets
      });

      this.fetch('/instances/aws','get', {}, 'AWS', (data) => {
        return _.values(data)
      });

    },
    render() {
      let component = this;
      function deleteMachine(provider, id) {
        component.fetch(`/instances/${provider}`,'del', { instanceId: id}, 'status');

      }
      function stopMachine(provider, id) {
        component.fetch(`/instances/${provider}/stop`,'post', { instanceId: id}, 'status');
      }
      function runMachine(provider, id) {
        component.fetch(`/instances/${provider}/run`,'post', { instanceId: id}, 'status');
      }

        var listDOItems = this.state.DO.map(function(item, index) {
            return (
              <tr>
                <td>DO</td>
                <td>{item.name}</td>
                <td>{item.image.distribution}</td>
                <td>{item.networks.v4[0] && item.networks.v4[0].ip_address}</td>
                <td>{item.status}</td>
                <td>{item.memory}</td>
                <td>{moment(item.created_at).format("dddd, MMMM Do YYYY, h:mm:ss a")}</td>
                <td>
                  <Button className={'floating'} bsSize='small' onClick={() => runMachine('do', item.id)}>Run</Button>
                  <Button className={'floating'} bsSize='small' onClick={() => stopMachine('do', item.id)}>Stop</Button>
                  <Button className={'floating'} bsSize='small' onClick={() => deleteMachine('do', item.id)}>Terminate</Button>
                  <Input className={'floating short-input'} type='text' placeholder='username' valueLink={component.linkState('usernameDo'+index)}/>
                  <ModalTrigger  className={'floating'} modal={<TerminalModal host={item.networks.v4[0] && item.networks.v4[0].ip_address} username={component.state['usernameDo'+index]}/>}>
                    <Button className={'floating'} bsStyle='primary' bsSize='small'>SSH</Button>
                  </ModalTrigger>

                </td>
              </tr>
              )
        });

      var listAWSItems = this.state.AWS.map(function(item, index) {
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
              <ModalTrigger  className={'floating'} modal={<AWSModal host={item.publicIpAddress}/>}>
                <Button className={'floating'} bsStyle='primary' bsSize='small'>SSH</Button>
              </ModalTrigger>
            </td>
          </tr>
        )
      });

        return(
          <div>
            <Loader loaded={this.state.DOLoaded && this.state.AWSLoaded}>
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
              <div>Latest Response: <pre>{JSON.stringify(this.state.status, null, 2)}</pre></div>
            </Loader>
          </div>);
    }
});


module.exports = Machines;
