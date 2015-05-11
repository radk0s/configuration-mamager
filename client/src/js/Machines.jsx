const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');
const {Grid, Row, Col, Table, Button} = require('react-bootstrap');
const async = require("async");


let Machines = React.createClass({
    getInitialState() {
        return {
            droplets: []
        }
    },
    componentDidMount() {
      this.listMachines();
      this.setState({
        interval: setInterval(this.listMachines,2000)
      });
    },
    componentWillUnmount() {
      clearInterval(this.interval);
    },
    listMachines() {
      let component = this;
      async.parallel({
          DO: function(callback){
            request
              .get('/instances/do')
              .set('authToken', auth.getToken())
              .set('Accept', 'application/json')
              .end((err, res) => {
                if (typeof res != "undefined" || res != null) {
                  callback(null, res.body);
                }
              });
          },
          AWS: function(callback){
            request
              .get('/instances/aws')
              .set('authToken', auth.getToken())
              .set('Accept', 'application/json')
              .end((err, res) => {
                if (typeof res != "undefined" || res != null) {
                  callback(null, res.body);
                }
              });
          }
        },
        function(err, results) {
          console.log(results);
          component.setState({
            DO: results.DO,
            AWS: results.AWS
          });
        });
    },
    createMachine() {
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

    render() {

        if (typeof this.state.DO == "undefined" ||
          typeof this.state.DO.droplets == "undefined" ||
          this.state.DO.droplets == null ||
          this.state.DO.droplets.length == 0) {
            return <p>No machines found for you.</p>
        }

        var listDOItems = this.state.DO.droplets.map(function(item) {
            console.log(item);
            function deleteMachine() {
              request
                .del('/instances/do')
                .set('authToken', auth.getToken())
                .set('Accept', 'application/json')
                .send({ instanceId: item.id})
                .end((err, res) => {
                  console.log(err);
                  console.log(res);
                });
            }
          function stopMachine() {
            request
              .post('/instances/do/stop')
              .set('authToken', auth.getToken())
              .set('Accept', 'application/json')
              .send({ instanceId: item.id})
              .end((err, res) => {
                console.log(err);
                console.log(res);
              });
          }
          function runMachine(run) {
            request
              .post('/instances/do/run')
              .set('authToken', auth.getToken())
              .set('Accept', 'application/json')
              .send({ instanceId: item.id})
              .end((err, res) => {
                console.log(err);
                console.log(res);
              });
          }
            return (
              <tr>
                <td>{item.name}</td>
                <td>{item.image.distribution}</td>
                <td>{item.networks.v4[0].ip_address}</td>
                <td>{item.status}</td>
                <td>{item.memory}</td>
                <td>{item.created_at}</td>
                <td>
                  <Button bsSize='large' onClick={runMachine}>Run</Button>
                  <Button bsSize='large' onClick={stopMachine}>Stop</Button>
                  <Button bsSize='large' onClick={deleteMachine}>Terminate</Button>
                </td>
              </tr>
              )
        });

      var listAWSItems = this.state.AWS.map(function(item) {
        console.log(item);

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
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {listDOItems}
              </tbody>
            </Table>
            <Button bsSize='large' onClick={this.createMachine}>Create instance</Button>
          </div>);
    }
});


module.exports = Machines;
