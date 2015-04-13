const React = require('react');
const auth = require("./Auth.js");

class Logout extends React.Component {
  componentDidMount () {
    auth.logout();
  }

  render () {
    return <p>You are now logged out</p>;
  }
}

module.exports = Logout;
