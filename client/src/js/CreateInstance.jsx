const React = require('react');
const Modal = require('react-bootstrap').Modal;
const Input = require('react-bootstrap').Input;
const request = require('superagent');
const auth = require("./Auth.js");
const moment = require('moment');
const _ = require('underscore');

module.exports =  React.createClass({
  getInitialState() {
    return {
      provider: 'DIGITAL_OCEAN',
      saveConf: false
    }
  },
  handleSelectChange() {
    this.setState({
      provider: this.refs.input.getValue()
    }, () => {
      console.log(this.state.provider);
    });
  },
  handleSelectConfChange() {
    let confId = parseInt(this.refs.configurations.getValue());
    console.log(confId);
    console.log(this.props.confs);

    let data = _.head(_.where(this.props.confs, {id: confId})).data;
    let awsConf = JSON.parse(data);
    awsConf.provider = 'AWS';
    this.setState(awsConf);
  },
  handleDOSubmit() {
    request
      .put('/instances/do')
      .set('authToken', auth.getToken())
      .set('Accept', 'application/json')
      .send({
        "name": "example.com",
        "region": "nyc3",
        "size": "512mb",
        "image": "ubuntu-14-04-x64",
        "ssh_keys": null,
        "backups": false,
        "ipv6": true,
        "user_data": null,
        "private_networking": null
      })
      .end((err, res) => {
        console.log(res);
      });
  },
  handleAWSSubmit (event) {
    var imageId = this.refs.imageId.getValue();
    var instanceType = this.refs.instanceType.getValue();
    var keyName = this.refs.keyName.getValue();
    var securityGroup = this.refs.securityGroup.getValue();

    let configuration = {
      imageId,
      instanceType,
      keyName,
      securityGroup
    };

    request
      .put('/instances/aws')
      .set('authToken', auth.getToken())
      .set('Accept', 'application/json')
      .send(configuration)
      .end((err, res) => {
      });

    request
      .post('/configuration')
      .set('authToken', auth.getToken())
      .set('Accept', 'application/json')
      .send({ name: 'AWS-' + moment().format(),
        provider: 'AWS',
        data: JSON.stringify(configuration)
      })
      .end((err, res) => {
      });
  },
  render() {
    let createForm;

      if (this.state.provider == 'AWS') {
        createForm = <form style={{width: '60%', 'margin-left': 10, 'margin-right': 'auto'}} onSubmit={this.handleAWSSubmit}>
          <Input type='text' value={this.state.imageId} onChange={() => { this.setState({imageId: this.refs.imageId.getValue()})}} label='imageId' ref={'imageId'}/>
          <Input type='text' value={this.state.instanceType} onChange={() => this.setState({instanceType: this.refs.instanceType.getValue()})} label='instanceType' ref={'instanceType'}/>
          <Input type='text' value={this.state.keyName} onChange={() => this.setState({keyName: this.refs.keyName.getValue()})} label='keyName' ref={'keyName'}/>
          <Input type='text' value={this.state.securityGroup} onChange={() => this.setState({securityGroup: this.refs.securityGroup.getValue()})} label='securityGroup' ref={'securityGroup'}/>
          <Input type='submit' value='Create AWS Instance'/>
        </form>;
      } else {
        createForm = <form style={{width: '60%', 'margin-left': 10, 'margin-right': 'auto'}} onSubmit={this.handleDOSubmit}>
          <Input type='submit' value='Create DO Instance'/>
        </form>
      }
    let options = this.props.confs.map((item, index) => {
      return  <option value={item.id} key={index}>{item.name}</option>
    });

    return (
      <Modal {...this.props} bsStyle="primary" title={ this.props.title }>
        <div style={{width: '140px', 'margin-left': 10}}>
          <h4>Select recent configuration:</h4>

          <Input type='select' ref='configurations' onChange={this.handleSelectConfChange}>
            {options}
          </Input>

          <h4>Select Provider:</h4>
          <Input type='select' ref='input' value={this.state.provider} onChange={this.handleSelectChange}>
            <option value={'DIGITAL_OCEAN'} key={0}>DO</option>
            <option value={'AWS'} key={1}>AWS</option>
          </Input>
        </div>
        {createForm}
      </Modal>
    );
  }
});
