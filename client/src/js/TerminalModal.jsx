const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const {Grid, Row, Col, Table, Button, Modal} = require('react-bootstrap');
const async = require("async");
const _ = require('underscore');
const moment = require('moment');

module.exports = React.createClass({
  getInitialState(){
    return {terminalConf:{}};
  },
  componentDidMount() {
    let component = this;
    request
      .post('/terminal')
      .set('authToken', auth.getToken())
      .set('Accept', 'application/json')
      .send({
        username: this.props.username,
        host: this.props.host})
      .end((err, res) => {
        setTimeout(() => {
          component.setState({
            terminalConf: res.body
          });
        }, 3000);
      });
  },
  render() {
    let iframe;
    let endpoint;
    if (this.state.terminalConf.port) {
      endpoint = `https://${window.location.hostname}:${this.state.terminalConf.port}/`;
      console.log(endpoint);
      iframe = <iframe src={endpoint} width='100%' height='100%'></iframe>
    }
    return (
      <Modal {...this.props} title={`SSH (try open (${endpoint}) if fails)`} dialogClassName='modal-body' animation={false}>
        {iframe}
      </Modal>
    );
  }
});
