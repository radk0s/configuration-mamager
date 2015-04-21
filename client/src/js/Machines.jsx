const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const {Grid, Row, Col, Table, Button} = require('react-bootstrap');



let Machines = React.createClass({
    getInitialState() {
        return {
            droplets: []
        }
    },
    componentDidMount() {
      this.listMachines();
    },
    listMachines() {
      request
        .get('/instances/do')
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .end((err, res) => {
          if (typeof res != "undefined" || res != null) {
            this.setState(res.body);
          }
        });
    },
    createMachine() {
      request
        .post('/instances/do')
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
          setTimeout(this.listMachines,500);
        });
    },

    render() {

        if (typeof this.state.droplets == "undefined" || this.state.droplets == null || this.state.droplets.length == 0) {
            return <p>No machines found for you.</p>
        }

        var listItems = this.state.droplets.map(function(item) {
            console.log(item);
            return (
              <tr>
                <td>{item.name}</td>
                <td>{item.image.distribution}</td>
                <td>{item.networks.v4[0].ip_address}</td>
                <td>{item.status}</td>
                <td>{item.memory}</td>
                <td>{item.created_at}</td>
              </tr>
              )
        });
        return(
          <div>
            <Table responsive>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Distribution</th>
                  <th>IPv4</th>
                  <th>Status</th>
                  <th>Memory</th>
                  <th>Created At</th>
                </tr>
              </thead>
              <tbody>
                {listItems}
              </tbody>
            </Table>
            <Button bsSize='large' onClick={this.createMachine}>Create instance</Button>
          </div>);
    }
});


module.exports = Machines;
