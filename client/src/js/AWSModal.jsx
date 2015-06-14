const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const {Grid, Row, Col, Table, Button, Modal} = require('react-bootstrap');
const async = require("async");
const _ = require('underscore');
const moment = require('moment');
const FetchDataMixin = require('./FetchDataMixin.js');

module.exports = React.createClass({
  mixins: [FetchDataMixin],
  getInitialState(){
    return {
      privateKey: ''
    };
  },
  componentDidMount() {
    let component = this;
    request
      .get('/hello')
      .set('authToken', auth.getToken())
      .set('Accept', 'application/json')
      .end((err, res) => {
        res.body.loaded = true;
        this.setState(res.body);
      });
  },
  render() {
    return (
      <Modal {...this.props} title='Modal heading' dialogClassName='modal-body' animation={false}>
        <p>AWS support ssh only with Private key.</p>
        <pre>{this.state.privateKey}</pre>
      </Modal>
    );
  }
});
