const React = require('react');
const auth = require('./Auth.js');

class Dashboard extends React.Component {
  render () {
    var token = auth.getToken();
    console.log(token);
    return (
      <div>
        <h1>Dashboard</h1>
        <p>{token}</p>
      </div>
    );
  }
}

module.exports = auth.requireAuth(Dashboard);
