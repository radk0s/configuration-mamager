const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const async = require("async");
const _ = require('underscore');
const {Grid, Row, Col, Table, Button, Input, Alert} = require('react-bootstrap');
const Loader = require('react-loader');
const FetchDataMixin = require('./FetchDataMixin.js');

let Backups = React.createClass({
  mixins: [React.addons.LinkedStateMixin, FetchDataMixin],
  getInitialState() {
    return {
      backups: [],
      DO: [],
      dropletName: "",
      backupsEnabled: false,
      backupsLoaded: true
    }
  },
  componentDidMount() {
    this.listDroplets();
    this.setState({
      interval: setInterval(this.listDroplets, 3000)
    });
  },
  componentWillUnmount() {
    clearInterval(this.state.interval);
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
    this.fetch('/instances/do','get', {}, 'DO', (data) => {
      return data.droplets
    }, () => {
      if (this.state.dropletName !== "") {
        component.listBackups()
      }
    });
    },

  listBackups() {
    this.fetch(`/backups/do/${this.state.dropletName}`,'get', {}, 'backups', (data) => {
      return data?data.backups:[]
    });
  },

  restore(imageId) {
    var instanceId = this.state.dropletId;

    this.fetch(`/instances/do/restore`,'post', { instanceId: instanceId, image: imageId}, 'status');
  },

  disableBackupsForDroplet() {
    var instanceId = this.state.dropletName;
    this.fetch(`/backups/do/disable`,'post', { instanceId: instanceId}, 'status');
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
            <Button bsSize='small' onClick={() => component.restore(item.id)}>Restore</Button>
          </td>
        </tr>
      )
    });

    return(
      <div>
        <Alert bsStyle='info'>
          Backups functionality is only provided for <strong>Digital Ocean</strong> machines.
        </Alert>
        <Loader loaded={this.state.DOLoaded}>
          <div style={{width: '40%', 'margin-left': 10}}>
            <Input type='select' value={this.state.dropletName} onChange={this.handleDropletNameChange} ref={'dropletName'} label="Select droplet name: ">
              <option value="" key="backup_placeholder" hidden>Please select...</option>
              {dropletsNamesDO}
            </Input>
            {this.state.backupsEnabled ? <Button bsSize='large' onClick={() => this.disableBackupsForDroplet()}>Disable backups for droplet</Button> : ''}
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
            <div>Latest Response: <pre>{JSON.stringify(this.state.status, null, 2)}</pre></div>
          </Loader>
        </Loader>
      </div>);
  }
});

module.exports = Backups;
