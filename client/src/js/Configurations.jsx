const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const {Grid, Row, Col, Table, Button} = require('react-bootstrap');


let Configurations = React.createClass({
  getInitialState() {
    return {
      configs: []
    }
  },
  componentDidMount() {
    this.listConfigurations();
    this.setState({
      interval: setInterval(this.listConfigurations ,2000)
    });
  },
  componentDidUnmount() {
    clearInterval(this.interval);
  },
  listConfigurations() {
    let component = this;
    request
      .get('/configuration')
      .set('authToken', auth.getToken())
      .set('Accept', 'application/json')
      .end((err, res) => {
        console.log(res.body);
        component.setState({
          configs: res.body
        });
      });
  },

  render() {

    function deleteConfig(name) {
      request
        .del(`/configuration`)
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .send({ name: name })
        .end((err, res) => {
          console.log(err);
          console.log(res);
        });
    }

    var configs = this.state.configs.map(function(item) {
      return (
        <tr>
          <td>{item.name}</td>
          <td>{item.provider}</td>
          <td>
            <Button bsSize='large' onClick={() => deleteConfig(item.name)}>Delete</Button>
          </td>
        </tr>
      )
    });

    return(
      <div>
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
      </div>);
  }
});

module.exports = Configurations;
