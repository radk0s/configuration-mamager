const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const async = require("async");
const _ = require('underscore');
const {Grid, Row, Col, Table, Button, Input} = require('react-bootstrap');
const Loader = require('react-loader');
const Modal = require('react-bootstrap').Modal;
const ModalTrigger = require('react-bootstrap').ModalTrigger;


let Snapshots = React.createClass({
  mixins: [React.addons.LinkedStateMixin],
  getInitialState() {
    return {
      snapshots: [],
      DO: [],
      AWS: [],
      dropletId: "",
      dropletProvider: "",
      dropletValue: "",
      awsAvailableDevices: [],
      awsVolumeIds: [],
      selectedVolume: "",
      volumeId: "",
      deviceName: "",
      snapshotsLoaded: false,
      loaded: false
    }
  },
  
  componentDidMount() {

    this.listDroplets();	
    this.getDevices();
    this.listSnapshots();
    this.setState({
      interval: setInterval(this.listDroplets, 1000)
    });
  },
  componentDidUnmount() {
    clearInterval(this.interval);
  },

  getVolumeId(item) {
    return item.blockDeviceMappings[0]?item.blockDeviceMappings[0].ebs.volumeId:null;
  },

  getVolumeIds(item){
	return item.blockDeviceMappings.map(function(device){
		  return device.ebs.volumeId;
	  });
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
    	  volumeId: dropletData[2],
    	  awsVolumeIds: new Array(dropletData.slice(2)),
    
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
        },
        component.getDevices(),
        component.listSnapshots(),
        
        () => { console.log("results fetched")});
      });
          component.getDevices(),
       this.listSnapshots();
  },

  getDevices() {
	  let id = this.state.dropletId;
	  let self = this;
	    request.get(`/devices/aws/${id}`)
	      .set('authToken', auth.getToken())
	      .set('Accept', 'application/json')
	      .end((err, res) => {
	        self.setState({
	
	          awsAvailableDevices: res.body
	         
	        });
	      });
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
          component.setState({
            snapshots: res.body?res.body.snapshots:[],
            snapshotsLoaded: true
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
      return <option value={`aws_${item.instanceId}_${component.getVolumeIds(item)}`} key={`aws_${index}`}>{item.instanceId}</option>;
    });

    var snapshots = this.state.snapshots.map(function(item) {
console.log(item);
      if (component.state.dropletProvider && component.state.dropletProvider === "do") {
        return (
          <tr>
            <td>{item.name}</td>
            <td>DO</td>
            <td>{item.created_at}</td>
            <td>
            	<Button bsSize='small' onClick={() => restore(item.id, component.state.deviceName, item.volumeId)}>Restore</Button>			              
            </td>
          </tr>
        )
      } else if(component.state.dropletProvider === "aws") {
     
        var devices = component.state.awsAvailableDevices.map(function(item) {
          return <option value={item}>{item} </option>;
        });
        console.log("devices")
        console.log(devices)
        return (
          <tr>
            <td>{item.snapshotId}</td>
            <td>AWS</td>
            <td>{item.startTime}</td>
            <td>
                <ModalTrigger modal={<Modal  {...component.props} title={`Restore snapshot`}>
					<div>
					 <Input type='select' value={component.state.deviceName} onChange={() => { component.setState({deviceName: component.refs.device.getValue()})}}  label='Select device' ref={'device'}>
					    <option value="" key="snapshot_placeholder" hidden>Please select...</option>
					 	{devices}
			         </Input>
					<Button bsSize='large' onClick={() => restore(item.snapshotId, component.state.deviceName, item.volumeId)}>Restore</Button>
				
			         </div>
			      </Modal>} >
              	<Button bsSize='small' >Restore</Button>
			</ModalTrigger>
            </td>
          </tr>
        )
      }

    });

    function restore(imageId, deviceName, volumeId) {
      var instanceId = component.state.dropletId;
      request
        .post(`/instances/${component.state.dropletProvider}/restore`)
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .send({ instance: instanceId, image: imageId, device: deviceName, volume: volumeId})
        .end((err, res) => {
          console.log(err);
          console.log(res);
        });
    }

    function createNewSnapshot() {  
    	
      var provider = component.state.dropletProvider;
      var instanceId = component.state.dropletId;
      if( provider === "aws" ) {
        instanceId = component.state.selectedVolume;
       }
      console.log("volume "+instanceId);
      
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
	 var volumes = component.state.awsVolumeIds.map(function(item) {
	        return <option value={item}>{item} </option>;
	      });
	    var volumesSelect;
	    if( component.state.dropletProvider === "aws" ) {
	    	volumesSelect=  <Input type='select' value={component.state.selectedVolume} onChange={() => { component.setState({
	    		selectedVolume: component.refs.selectedVolume.getValue()},  () => {   console.log("volume2 +"+this.state.selectedVolume)}
	    	)}
	    	}  label='Select volume' ref={'selectedVolume'}>
	        <option value="" key="snapshot_placeholder" hidden>Please select...</option>
	        {volumes}
	        </Input>
	      }
	    console.log("volume2 +"+this.state.selectedVolume);
    return(
      <div>
        <Loader loaded={this.state.loaded}>
          <div style={{width: '40%', 'margin-left': 10}}>
            <Input type='select' value={this.state.dropletValue} onChange={this.handleDropletNameChange} ref={'dropletId'} label="Select droplet name: ">
              <option value="" key="snapshot_placeholder" hidden>Please select...</option>
              {dropletsNamesDO}
              {dropletsNamesAWS}
            </Input>
        {volumesSelect}
            <Input type='text' value={this.state.newSnapshotName} onChange={() => { this.setState({newSnapshotName: this.refs.newSnapshotName.getValue()})}} label='Create new snapshot for selected droplet:' ref={'newSnapshotName'}/>
            <Button bsSize='large' onClick={() => createNewSnapshot()}>Create new snapshot</Button>
          </div>
          <Loader loaded={this.state.snapshotsLoaded}>
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
          </Loader>
        </Loader>
      </div>);
  }
});

module.exports = Snapshots;
