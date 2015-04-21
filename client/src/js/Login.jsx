const React = require('react');
const auth = require('./Auth.js');
const {Grid, Row, Col, Input, Button} = require('react-bootstrap');

class Login extends React.Component {

  constructor () {
    this.handleSubmit = this.handleSubmit.bind(this);
    this.state = {
      error: false
    };
  }

  handleSubmit (event) {
    event.preventDefault();
    var { router } = this.context;
    var nextPath = router.getCurrentQuery().nextPath;
    var email = this.refs.email.getValue();
    var pass = this.refs.pass.getValue();
    auth.login(email, pass, (loggedIn) => {
      if (!loggedIn)
        return this.setState({ error: true });
      if (nextPath) {
        router.replaceWith(nextPath);
      } else {
        router.replaceWith('/dashboard');
      }
    });
  }

  render () {
    return (
      <form onSubmit={this.handleSubmit}>
        <Row>
          <Input type='text' label='Email' labelClassName='col-xs-offset-3 col-xs-1' wrapperClassName='col-xs-2' ref={'email'}/>
        </Row>
        <Row>
          <Input type='password' label='Password' labelClassName='col-xs-offset-3 col-xs-1' wrapperClassName='col-xs-2' ref={'pass'}/>
        </Row>
        <Row/>
        <Row>
          <Input wrapperClassName='col-xs-offset-4 col-xs-1' type='submit' value='Log in  ' help={this.state.error && 'Bad login information'}/>

        </Row>
      </form>
    );
  }
}

Login.contextTypes = {
  router: React.PropTypes.func
};

module.exports = Login;
