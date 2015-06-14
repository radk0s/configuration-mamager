const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const {Grid, Row, Col, Table, Button} = require('react-bootstrap');
const Loader = require('react-loader');
const FetchDataMixin = require('./FetchDataMixin.js');


let Configurations = React.createClass({
  mixins: [FetchDataMixin],
  getInitialState() {
    return {
      configs: [],
      configsLoaded: false
    }
  },
  componentDidMount() {
    this.listConfigurations();
    this.setState({
      interval: setInterval(this.listConfigurations ,7000)
    });
  },
  componentWillUnmount() {
    clearInterval(this.interval);
  },
  listConfigurations() {
    this.fetch('/configuration','get', {}, 'configs');
  },

  deleteConfig(name) {
    this.fetch('/configuration','del', {name: name});
  },
  render() {
    let component = this;
    var configs = this.state.configs.map(function(item) {
      return (
        <tr>
          <td>{item.name}</td>
          <td>{item.provider}</td>
          <td>
            <Button bsSize='large' onClick={() => component.deleteConfig(item.name)}>Delete</Button>
          </td>
        </tr>
      )
    });

    return(
      <div>
        <Loader loaded={this.state.configsLoaded}>
        <Table responsive>
          <thead>
          <tr>
            <th>Name</th>
            <th>Provider</th>
          </tr>
          </thead>
          <tbody>
            {configs}
          </tbody>
        </Table>
        </Loader>
      </div>);
  }
});

module.exports = Configurations;
