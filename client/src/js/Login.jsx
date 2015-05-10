const React = require('react');
const auth = require('./Auth.js');
const Joi = require('joi');
const {Grid, Row, Col, Input, Button} = require('react-bootstrap');

class Login extends React.Component {

  validateEmail() {
    var emailState ="";
    Joi.validate(this.state.email, Joi.string().email(), function(err, value) {
      if(err != null) {
        emailState = 'error'
      } else {
        emailState = 'success'
      }
    });
    return emailState;
  }

  validatePassword() {
    var passState ="";
    Joi.validate(this.state.pass, Joi.string().regex(/[a-zA-Z0-9]{3,30}/), function(err, value) {
      if(err != null) {
        passState = 'error'
      } else {
        passState = 'success'
      }
    });
    return passState;
  }


  constructor () {
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.state = {
      error: false,
      email: '',
      pass: ''
    };
  }

  handleChange(event) {
    var error = this.state.error;
    this.setState({
      error: error,
      email: this.refs.email.getValue(),
      pass: this.refs.pass.getValue()
    });
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
          <Input type='text' label='Email' labelClassName='col-xs-offset-3 col-xs-1' value={this.state.email} wrapperClassName='col-xs-2' ref={'email'} onChange={this.handleChange} bsStyle={this.validateEmail()}/>
        </Row>
        <Row>
          <Input type='password' label='Password' labelClassName='col-xs-offset-3 col-xs-1' value={this.state.pass} wrapperClassName='col-xs-2' ref={'pass'} onChange={this.handleChange} bsStyle={this.validatePassword()}/>
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
