const React = require('react');
const auth = require('./Auth.js');
const request = require('superagent');

let Dashboard = React.createClass({
    getInitialState() {
      return {
        userToken: "",
        userEmail: "",
        userDOToken: "",
        userAWSToken: ""
      }
    },
    componentDidMount() {
      request
        .get('/hello')
        .set('X-Auth-Token', auth.getToken())
        .set('Accept', 'application/json')
        .end((err, res) => {
          console.log(res.body);
          this.setState(res.body);
        });
    },
    render() {
      var token = auth.getToken();
      console.log(this);
      return (
        <div>
          <h1>Dashboard</h1>
          <p>User Email: {this.state.userEmail}</p>
          <p>DO Token: {this.state.userDOToken}</p>
          <p>AWS Token: {this.state.userAWSToken}</p>
        </div>
      );
    }
  });

module.exports = Dashboard;
