const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const async = require("async");
const _ = require('underscore');
const {Grid, Row, Col, Table, Button, Input} = require('react-bootstrap');
const Loader = require('react-loader');
const Modal = require('react-bootstrap').Modal;
const ModalTrigger = require('react-bootstrap').ModalTrigger;
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
      interval: setInterval(this.listDroplets, 5000)
    });
  },
  componentWillUnmount() {
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
    	  var volumes = dropletData[2].split(",");
    	  this.setState({
    	  volumeId: volumes[0],
    	  awsVolumeIds: volumes,
    	  selectedVolume: volumes[0]
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

  getDevices() {
	  let id = this.state.dropletId;
	  let self = this;
	  var awsVolumes=this.state.awsVolumeIds?this.state.awsVolumeIds:[];
	  this.fetch(`/devices/aws/`, 'get', {awsVolumes}, "awsAvailableDevices", (data) => {
	        self.setState({
	                deviceName: data?data[0]:[]
	        });
	        return data ? data : []
	    });
  },
  
  
  listSnapshots() {
    let component = this;
    let id = this.state.dropletId;
    if( this.state.dropletProvider === "aws" ) {
      id = this.state.volumeId;
    }
    var awsVolumes=this.state.awsVolumeIds?this.state.awsVolumeIds:[];
    this.fetch(`/snapshots/${this.state.dropletProvider}/`,'get', {awsVolumes}, 'snapshots', (data) => {
      return data ? data.snapshots : []
    });
  },

  restore(imageId) {
	    var instanceId = this.state.dropletId;
	    this.fetch(`/instances/${this.state.dropletProvider}/restore`,'post', { instanceId: instanceId, image: imageId, device: deviceName, volume: volumeId}, 'snapshotStatus');

	  },
createNewSnapshot() {
	    var provider = this.state.dropletProvider;
	    var instanceId = this.state.dropletId;
	    if( provider === "aws" ) {
	      instanceId = this.state.selectedVolume;
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
      return <option value={`aws_${item.instanceId}_${component.getVolumeIds(item)}`} key={`aws_${index}`}>{item.instanceId}</option>;
    });

    var snapshots = this.state.snapshots.map(function(item) {
      if (component.state.dropletProvider && component.state.dropletProvider === "do") {
        return (
          <tr>
            <td>{item.name}</td>
            <td>DO</td>
            <td>{item.created_at}</td>
            <td>
            	<Button bsSize='small' onClick={() => component.restore(item.id, component.state.deviceName, item.volumeId)}>Restore</Button>			              
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


  
     let button = this.state.newSnapshotName?<Button bsSize='large' onClick={() => this.createNewSnapshot()}>Create new snapshot</Button>:<div></div>
	 var volumes = component.state.awsVolumeIds.map(function(item) {
	        return <option value={item}>{item} </option>;
	      });
	    var volumesSelect;
	    if( component.state.dropletProvider === "aws" ) {
	    	volumesSelect=  <Input type='select' value={component.state.selectedVolume} onChange={() => { component.setState({
	    		selectedVolume: component.refs.selectedVolume.getValue()},  () => {   console.log("volume2 +"+this.state.selectedVolume)}
	    	)}
	    	}  label='Select volume' ref={'selectedVolume'}>
	        {volumes}
	        </Input>
	      }
	    console.log("volume2 +"+this.state.selectedVolume);
    return(
      <div>
         <Loader loaded={this.state.DOLoaded && this.state.AWSLoaded}>
          <div style={{width: '40%', 'margin-left': 10}}>
            <Input type='select' value={this.state.dropletValue} onChange={this.handleDropletNameChange} ref={'dropletId'} label="Select droplet name: ">
              <option value="" key="snapshot_placeholder" hidden>Please select...</option>
              {dropletsNamesDO}
              {dropletsNamesAWS}
            </Input>
        {volumesSelect}
           <Input type='text' value={this.state.newSnapshotName} onChange={() => { this.setState({newSnapshotName: this.refs.newSnapshotName.getValue()})}} label='Create new snapshot for selected droplet:' ref={'newSnapshotName'}/>
            {button}
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
            <div>Latest Response: <pre>{JSON.stringify(this.state.snapshotStatus, null, 2)}</pre></div>
          </Loader>
        </Loader>
      </div>);
  }
});

module.exports = Snapshots;
