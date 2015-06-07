const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const async = require("async");
const _ = require('underscore');
const {Grid, Row, Col, Table, Button, Input} = require('react-bootstrap');


let Snapshots = React.createClass({
  mixins: [React.addons.LinkedStateMixin],
  getInitialState() {
    return {
      snapshots: [],
      DO: [],
      AWS: [],
      dropletId: "",
      dropletProvider: "",
      dropletValue: ""
    }
  },
  componentDidMount() {
    this.listDroplets();
    this.listSnapshots();
    this.setState({
      interval: setInterval(this.listDroplets, 1000)
    });
  },
  componentDidUnmount() {
    clearInterval(this.interval);
  },

  getVolumeId(item) {
    return item.blockDeviceMappings[0].ebs.volumeId;
  },

  handleDropletNameChange() {
    var dropletData = this.refs.dropletId.getValue().split("_");
    var provider = dropletData[0];
    this.setState({
      dropletProvider: provider,
      dropletId: dropletData[1],
      dropletValue: this.refs.dropletId.getValue()
    });
    if( provider === "aws") {
      this.setState({
        volumeId: dropletData[2]
      });
    }
  },

  listDroplets() {
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
    this.listSnapshots();
  },

  listSnapshots() {
      let component = this;
      let id = this.state.dropletId;
      if( this.state.dropletProvider === "aws" ) {
        id = this.state.volumeId;
      }
      request
        .get(`/snapshots/${this.state.dropletProvider}/${id}`)
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .end((err, res) => {
          console.log(res.body);
          component.setState({
            snapshots: res.body.snapshots
          });
        });
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
              <Button bsSize='small' onClick={() => restore(item.id)}>Restore</Button>
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
              <Button bsSize='small' onClick={() => restore(item.snapshotId)}>Restore</Button>
            </td>
          </tr>
        )
      }

    });

    function restore(imageId) {
      var instanceId = component.state.dropletId;
      request
        .post(`/instances/${component.state.dropletProvider}/restore`)
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .send({ instanceId: instanceId, image: imageId})
        .end((err, res) => {
          console.log(err);
          console.log(res);
        });
    }

    function createNewSnapshot() {
      var provider = component.state.dropletProvider;
      var instanceId = component.state.dropletId;
      if( provider === "aws" ) {
        instanceId = component.state.volumeId;
      }
      var snapshotName = component.state.newSnapshotName;

      if( snapshotName && snapshotName != "" ) {
        request
          .post(`/instances/${provider}/snapshot`)
          .set('authToken', auth.getToken())
          .set('Accept', 'application/json')
          .send({instanceId: instanceId, name: snapshotName})
          .end((err, res) => {
            console.log(err);
            console.log(res);
          });
      }
    }

    return(
      <div>
        <div style={{width: '40%', 'margin-left': 10}}>
          <Input type='select' value={this.state.dropletValue} onChange={this.handleDropletNameChange} ref={'dropletId'} label="Select droplet name: ">
            {dropletsNamesDO}
            {dropletsNamesAWS}
          </Input>
          <Input type='text' value={this.state.newSnapshotName} onChange={() => { this.setState({newSnapshotName: this.refs.newSnapshotName.getValue()})}} label='Create new snapshot for selected droplet:' ref={'newSnapshotName'}/>
          <Button bsSize='large' onClick={() => createNewSnapshot()}>Create new snapshot</Button>
        </div>
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
      </div>);
  }
});

module.exports = Snapshots;
