const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const async = require("async");
const _ = require('underscore');
const {Grid, Row, Col, Table, Button, Input} = require('react-bootstrap');
const Loader = require('react-loader');
const FetchDataMixin = require('./FetchDataMixin.js');

let Snapshots = React.createClass({
  mixins: [FetchDataMixin],
  getInitialState() {
    return {
      snapshots: [],
      DO: [],
      AWS: [],
      dropletId: "",
      dropletProvider: "",
      dropletValue: "",
      snapshotsLoaded: false,
      loaded: false
    }
  },
  componentDidMount() {
    this.listDroplets();
    this.setState({
      interval: setInterval(this.listDroplets, 5000)
    });
  },
  componentWillUnmount() {
    clearInterval(this.interval);
  },

  getVolumeId(item) {
    return item.blockDeviceMappings[0]?item.blockDeviceMappings[0].ebs.volumeId:null;
  },

  handleDropletNameChange() {
    var dropletData = this.refs.dropletId.getValue().split("_");
    var provider = dropletData[0];
    this.setState({
      dropletProvider: provider,
      dropletId: dropletData[1],
      dropletValue: this.refs.dropletId.getValue(),
      snapshotsLoaded: false
    });
    if( provider === "aws") {
      this.setState({
        volumeId: dropletData[2]
      });
    }
  },

  listDroplets() {
    let component = this;

    function listSnapshots() {
      if(component.state.DOLoaded && component.state.AWSLoaded) {
        if(component.state.dropletProvider == "") return;
        component.listSnapshots()
      }
    }

    this.fetch('/instances/do','get', {}, 'DO', (data) => {
      return data.droplets
    }, listSnapshots);

    this.fetch('/instances/aws','get', {}, 'AWS', (data) => {
      return _.values(data)
    }, listSnapshots);

  },

  listSnapshots() {
    let component = this;
    let id = this.state.dropletId;
    if( this.state.dropletProvider === "aws" ) {
      id = this.state.volumeId;
    }

    this.fetch(`/snapshots/${this.state.dropletProvider}/${id}`,'get', {}, 'snapshots', (data) => {
      return data ? data.snapshots : []
    });
  },

  restore(imageId) {
    var instanceId = this.state.dropletId;
    this.fetch(`/instances/${this.state.dropletProvider}/restore`,'post', { instanceId: instanceId, image: imageId});

  },
  createNewSnapshot() {
    var provider = this.state.dropletProvider;
    var instanceId = this.state.dropletId;
    if( provider === "aws" ) {
      instanceId = this.state.volumeId;
    }
    var snapshotName = this.state.newSnapshotName;

    if( snapshotName && snapshotName != "" ) {
      this.fetch(`/instances/${provider}/snapshot`,'post', {instanceId: instanceId, name: snapshotName}, 'snapshotStatus');
    }
  },

  render() {
    var component = this;

    var dropletsNamesDO = this.state.DO.map(function(item, index) {
      return <option value={`do_${item.id}`} key={`do_${index}`}>{item.name}</option>;
    });

    var dropletsNamesAWS = this.state.AWS.map(function(item, index) {
      let item = item[0];
      return <option value={`aws_${item.instanceId}_${component.getVolumeId(item)}`} key={`aws_${index}`}>{item.instanceId}</option>;
    });

    var snapshots = this.state.snapshots.map(function(item) {
      if (component.state.dropletProvider && component.state.dropletProvider === "do") {
        return (
          <tr>
            <td>{item.name}</td>
            <td>DO</td>
            <td>{item.created_at}</td>
            <td>
              <Button bsSize='small' onClick={() => this.restore(item.id)}>Restore</Button>
            </td>
          </tr>
        )
      } else if(component.state.dropletProvider === "aws") {
        console.log(item);
        return (
          <tr>
            <td>{item.snapshotId}</td>
            <td>AWS</td>
            <td>{item.startTime}</td>
            <td>
              <Button bsSize='small' onClick={() => this.restore(item.snapshotId)}>Restore</Button>
            </td>
          </tr>
        )
      }
    });

    let button = this.state.newSnapshotName?<Button bsSize='large' onClick={() => this.createNewSnapshot()}>Create new snapshot</Button>:<div></div>

    return(
      <div>
        <Loader loaded={this.state.DOLoaded && this.state.AWSLoaded}>
          <div style={{width: '40%', 'margin-left': 10}}>
            <Input type='select' value={this.state.dropletValue} onChange={this.handleDropletNameChange} ref={'dropletId'} label="Select droplet name: ">
              <option value="" key="snapshot_placeholder" hidden>Please select...</option>
              {dropletsNamesDO}
              {dropletsNamesAWS}
            </Input>

          </div>
          <Loader loaded={this.state.snapshotsLoaded}>
            <Input type='text' value={this.state.newSnapshotName} onChange={() => { this.setState({newSnapshotName: this.refs.newSnapshotName.getValue()})}} label='Create new snapshot for selected droplet:' ref={'newSnapshotName'}/>
            {button}
            <Table responsive>
              <thead>
              <tr>
                <th>Name</th>
                <th>Provider</th>
                <th>Created at</th>
                <th>Actions</th>
              </tr>
              </thead>
              <tbody>
              {snapshots}
              </tbody>
            </Table>
            <div>Latest Response: <pre>{JSON.stringify(this.state.snapshotStatus, null, 2)}</pre></div>
          </Loader>
        </Loader>
      </div>);
  }
});

module.exports = Snapshots;
