const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const async = require("async");
const _ = require('underscore');
const {Grid, Row, Col, Table, Button, Input, Alert} = require('react-bootstrap');
const Loader = require('react-loader');



let Backups = React.createClass({
  mixins: [React.addons.LinkedStateMixin],
  getInitialState() {
    return {
      backups: [],
      DO: [],
      dropletName: "",
      backupsEnabled: false,
      backupsLoaded: false,
      loaded: false
    }
  },
  componentDidMount() {
    this.listDroplets();
    this.listBackups();
    this.setState({
      interval: setInterval(this.listDroplets, 2000)
    });
  },
  componentDidUnmount() {
    clearInterval(this.interval);
  },

  handleDropletNameChange() {
    var dropletName = this.refs.dropletName.getValue();
    var backupsEnabled = false;
    this.state.DO.map(function(item) {
      if( item.id == dropletName && _.contains(item.features, "backups") === true ) {
        backupsEnabled = true;
      }
    });

    this.setState({
      dropletName: dropletName,
      backupsEnabled: backupsEnabled,
      backupsLoaded: false
    });
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
        }
      },
      function(err, results) {
        component.setState({
          DO: results.DO,
          loaded: true
        },
        component.listBackups(),
        () => { console.log("results fetched")});
      });
  },

  listBackups() {
    let component = this;
    request
      .get(`/backups/do/${this.state.dropletName}`)
      .set('authToken', auth.getToken())
      .set('Accept', 'application/json')
      .end((err, res) => {
        console.log(res.body);
        component.setState({
          backups: res.body?res.body.backups:[],
          backupsLoaded: true
        });
      });
  },

  render() {

    var component = this;

    var dropletsNamesDO = this.state.DO.map(function(item, index) {
      if ( _.size(item.backup_ids) > 0 || _.contains(item.features, "backups") === true ) {
        return <option value={item.id} key={index}>{item.name}</option>;
      }

    });

    var backups = this.state.backups.map(function(item) {
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
    });

    function restore(imageId) {
      var instanceId = component.state.dropletId;
      request
        .post(`/instances/do/restore`)
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .send({ instanceId: instanceId, image: imageId})
        .end((err, res) => {
          console.log(err);
          console.log(res);
        });
    }

    function disableBackupsForDroplet() {
      var instanceId = component.state.dropletName;
      request
        .post(`/backups/do/disable`)
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .send({ instanceId: instanceId})
        .end((err, res) => {
          console.log(err);
          console.log(res);
        });
    }

    return(
      <div>
        <Alert bsStyle='info'>
          Backups functionality is only provided for <strong>Digital Ocean</strong> machines.
        </Alert>
        <Loader loaded={this.state.loaded}>
          <div style={{width: '40%', 'margin-left': 10}}>
            <Input type='select' value={this.state.dropletName} onChange={this.handleDropletNameChange} ref={'dropletName'} label="Select droplet name: ">
              <option value="" key="backup_placeholder" hidden>Please select...</option>
              {dropletsNamesDO}
            </Input>
            {this.state.backupsEnabled ? <Button bsSize='large' onClick={() => disableBackupsForDroplet()}>Disable backups for droplet</Button> : ''}
          </div>
          <Loader loaded={this.state.backupsLoaded}>
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
              {backups}
              </tbody>
            </Table>
          </Loader>
        </Loader>
      </div>);
  }
});

module.exports = Backups;
