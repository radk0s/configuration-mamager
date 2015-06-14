const React = require('react');
const Modal = require('react-bootstrap').Modal;
const Input = require('react-bootstrap').Input;
const request = require('superagent');
const auth = require("./Auth.js");
const moment = require('moment');
const _ = require('underscore');
const Loader = require('react-loader');
const FetchDataMixin = require('./FetchDataMixin.js');

module.exports =  React.createClass({
  mixins: [FetchDataMixin],
  getInitialState() {
    return {
      provider: 'DIGITAL_OCEAN',
      saveConf: false,
      DORegions: [],
      allDORegions: [],
      allDOSizes: [],
      DOImages: [],
      DOSizes: [],
      AWSImages: [],
      AWSInstanceTypes: [],
      AWSKeys: [],
      AWSSecurityGroups: [],
      isDOSelectedFromConfiguration: false,
      createInstanceLoaded: true,
      configurations: ""
    }
  },

  isConfigurationConnectedWithChosenProvider(name) {
    let provider = this.state.provider;
    return (name.includes('AWS') && provider === 'AWS') || (name.includes('DO') && provider === 'DIGITAL_OCEAN');
  },
  handleSelectChange() {
    this.setState({
      provider: this.refs.input.getValue()
    }, () => {
      console.log(this.state.provider);
    });
  },
  componentDidMount() {
    let self = this;
    this.fetch('/images/do','get', {}, 'DOImages', (data) => {
      return data.images
    });
    //
    this.fetch('/regions/do','get', {}, 'allDORegions', (data) => {
      return data.regions
    });

    this.fetch('/sizes/do','get', {}, 'allDOSizes', (data) => {
      return data.sizes
    });

    this.fetch('/imageids/aws','get', {}, 'AWSImages');

    this.fetch('/instancetypes/aws','get', {}, 'AWSInstanceTypes');

    this.fetch('/keynames/aws','get', {}, 'AWSKeys');

    this.fetch('/securitygroups/aws', 'get', {}, 'AWSSecurityGroups');
  },
  componentDidUpdate() {
    this.render();
  },
  handleDOImageSelect() {

    this.setState({
      isDOSelectedFromConfiguration: false
    });

    let selectedImage = this.refs.image.getValue();
    var self = this;
    this.setState({
      image: selectedImage
    });
    this.state.DOImages.map(function(item) {
      if(item.slug === selectedImage) {
        self.setState({
          DORegions: item.regions
        });
      }
    });
  },
  handleDORegionSelect() {

    this.setState({
      isDOSelectedFromConfiguration: false
    });

    let selectedRegion = this.refs.region.getValue();
    var self = this;
    this.setState({
      region: selectedRegion
    });
    this.state.allDORegions.map(function(item) {
      if(item.slug === selectedRegion) {
        self.setState({
          DOSizes: item.sizes
        });
      }
    });
  },
  handleDOSizeSelect(){

    let selectedSize = this.refs.size.getValue();

    this.setState({
      isDOSelectedFromConfiguration: false,
      size: selectedSize
    });

  },
  handleSelectConfChange() {

    this.setState({
      isDOSelectedFromConfiguration: true,
      configurations: this.refs.configurations.getValue()
    });

    let confId = parseInt(this.refs.configurations.getValue());
    console.log(confId);
    console.log(this.props.confs);

    let data = _.head(_.where(this.props.confs, {id: confId})).data;
    let conf = JSON.parse(data);
    conf.provider = this.state.provider;
    this.setState(conf);
  },
  handleDOSubmit(event) {
    event.preventDefault();

    this.setState({
      createInstanceLoaded: false
    });

    let component = this;

    var saveConfig = this.refs.saveConfig.getValue();

    let configuration = {
      "name": this.refs.name.getValue(),
      "region": this.refs.region.getValue(),
      "size": this.refs.size.getValue(),
      "image": this.refs.image.getValue(),
      "ssh_keys": null,
      "backups": this.refs.backups.getValue(),
      "ipv6": true,
      "user_data": null,
      "private_networking": null
    };

    var configurationData = {
      name: 'DO-' + moment().format(),
      provider: 'DIGITAL_OCEAN',
      data: JSON.stringify(configuration)
    };

    this.fetch("/instances/do", 'put', configuration, (saveConfig === "yes") ? null : "createInstance", null, () => {
        if (saveConfig === "yes") {
          component.fetch("/configuration", "post", configurationData, "createInstance", null, null);
        }
      }
    );

  },
  handleAWSSubmit (event) {
    event.preventDefault();
    this.setState({
      createInstanceLoaded: false
    });

    let component = this;

    var imageId = this.refs.imageId.getValue();
    var instanceType = this.refs.instanceType.getValue();
    var keyName = this.refs.keyName.getValue();
    var securityGroup = this.refs.securityGroup.getValue();
    var saveConfig = this.refs.saveConfig.getValue();

    let configuration = {
      imageId,
      instanceType,
      keyName,
      securityGroup
    };

    var configurationData = {
      name: 'AWS-' + moment().format(),
      provider: 'AWS',
      data: JSON.stringify(configuration)
    };

    this.fetch("/instances/aws", 'put', configuration, (saveConfig === "yes") ? null : "createInstance", null, () => {
      if( saveConfig === "yes" ) {
        component.fetch("/configuration", "post", configurationData, "createInstance", null, null);
      }
    });

  },
  render() {
    let createForm;

      if (this.state.provider == 'AWS') {
        var imageIds = this.state.AWSImages.map(function(item, index) {
          return <option value={item.id} key={index}>{item.name}</option>;
        });
        var instanceTypes = this.state.AWSInstanceTypes.map(function(item, index) {
          return <option value={item} key={index}>{item}</option>;
        });
        var keyNames = this.state.AWSKeys.map(function(item, index) {
          return <option value={item} key={index}>{item}</option>;
        });
        var securityGroups = this.state.AWSSecurityGroups.map(function(item, index) {
          return <option value={item} key={index}>{item}</option>;
        });
        createForm = <form style={{width: '60%', 'margin-left': 10, 'margin-right': 'auto'}} onSubmit={this.handleAWSSubmit}>
          <Input type='select' value={this.state.imageId} onChange={() => { this.setState({imageId: this.refs.imageId.getValue()})}} label='imageId' ref={'imageId'}>
            {imageIds}
          </Input>
          <Input type='select' value={this.state.instanceType} onChange={() => this.setState({instanceType: this.refs.instanceType.getValue()})} label='instanceType' ref={'instanceType'}>
            {instanceTypes}
          </Input>
          <Input type='select' value={this.state.keyName} onChange={() => this.setState({keyName: this.refs.keyName.getValue()})} label='keyName' ref={'keyName'}>
            {keyNames}
          </Input>
          <Input type='select' value={this.state.securityGroup} onChange={() => this.setState({securityGroup: this.refs.securityGroup.getValue()})} label='securityGroup' ref={'securityGroup'}>
            {securityGroups}
          </Input>
          <Input type='select' value={this.state.saveConfig} onChange={() => { this.setState({saveConfig: this.refs.saveConfig.getValue()})}} label='Save Configuration?' ref={'saveConfig'}>
            <option value="no" key="1">No</option>
            <option value="yes" key="2">Yes</option>
          </Input>
          <Input type='submit' value='Create AWS Instance'/>
        </form>;
      } else {
        var self = this;
        var images = this.state.DOImages.map(function(item, index) {
          return <option value={item.slug} key={index}>{item.slug}</option>;
        });
        var regions = this.state.allDORegions.map(function(item, index) {
          if (self.state.isDOSelectedFromConfiguration){
            return <option value={item.slug} key={index}>{item.name}</option>;
          }
          return self.state.DORegions.map(function(availableitem) {
            if(availableitem === item.slug) {
              return <option value={item.slug} key={index}>{item.name}</option>;
            }
          });
        });
        var sizes = this.state.allDOSizes.map(function(item, index) {
          if (self.state.isDOSelectedFromConfiguration){
            return <option value={item.slug} key={index}>{item.slug}</option>;
          }
          return self.state.DOSizes.map(function(availableitem) {
            if(availableitem === item.slug) {
              return <option value={item.slug} key={index}>{item.slug}</option>;
            }
          });
        });
        createForm = <form style={{width: '60%', 'margin-left': 10, 'margin-right': 'auto'}} onSubmit={this.handleDOSubmit}>
          <Input type='text' value={this.state.name} onChange={() => { this.setState({name: this.refs.name.getValue()})}} label='name' ref={'name'} help='Name can only contain alphanumeric characters, dashes, and periods.'/>
          <Input type='select' value={this.state.image} onChange={this.handleDOImageSelect} label='image' ref={'image'}>
            {images}
          </Input>
          <Input type='select' value={this.state.region} onChange={this.handleDORegionSelect} label='region' ref={'region'}>
            {regions}
          </Input>
          <Input type='select' value={this.state.size} onChange={this.handleDOSizeSelect} label='size' ref={'size'}>
            {sizes}
          </Input>
          <Input type='select' value={this.state.backups} onChange={() => { this.setState({backups: this.refs.backups.getValue()})}} label='Enable automatic backups?' ref={'backups'}>
            <option value="false" key="1">No</option>
            <option value="true" key="2">Yes</option>
          </Input>
          <Input type='select' value={this.state.saveConfig} onChange={() => { this.setState({saveConfig: this.refs.saveConfig.getValue()})}} label='Save Configuration?' ref={'saveConfig'}>
            <option value="no" key="1">No</option>
            <option value="yes" key="2">Yes</option>
          </Input>
          <Input type='submit' value='Create DO Instance'/>
        </form>
      }
    var self = this;
    let options = this.props.confs.map((item, index) => {
      if(self.isConfigurationConnectedWithChosenProvider(item.name)) {
        return <option value={item.id} key={index}>{item.name}</option>
      }
    });

    return (
      <Modal {...this.props} bsStyle="primary" title={ this.props.title }>
        <div style={{width: '140px', 'margin-left': 10}}>

          <h4>Select Provider:</h4>
          <Input type='select' ref='input' value={this.state.provider} onChange={this.handleSelectChange}>
            <option value={'DIGITAL_OCEAN'} key={0}>DO</option>
            <option value={'AWS'} key={1}>AWS</option>
          </Input>

          <h4>Select recent configuration:</h4>

          <Input type='select' ref='configurations' value={this.state.configurations} onChange={this.handleSelectConfChange}>
            <option value="" key="conf_placeholder" hidden>Please select...</option>
            {options}
          </Input>
        </div>
        <Loader loaded={this.state.createInstanceLoaded}>
        {createForm}
        </Loader>
      </Modal>
    );
  }
});
