const React = require('react');
const auth = require('./Auth.js');
const {Grid, Row, Col, Input, Button} = require('react-bootstrap');
const Joi = require('joi');

class Signup extends React.Component {

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

  validateToken(token) {
    var tokenState = "";
    var value;
    if(token === "AWSAccessKey") {
      value = this.state.AWSAccessKey;
    } else if(token === "AWSSecretKey") {
      value = this.state.AWSSecretKey;
    } else {
      value = this.state.DOToken;
    }
    Joi.validate(value, Joi.string().required(), function(err, value) {
      if(err != null) {
        tokenState = 'error'
      } else {
        tokenState = 'success'
      }
    });
    return tokenState;
  }

  handleChange(event) {
    var error = this.state.error;
    var success = this.state.success;
    this.setState({
      error: error,
      success: success,
      email: this.refs.email.getValue(),
      pass: this.refs.pass.getValue(),
      DOToken: this.refs.DOToken.getValue(),
      AWSAccessKey: this.refs.AWSAccessKey.getValue(),
      AWSSecretKey: this.refs.AWSSecretKey.getValue()
    });
  }

  constructor () {
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.state = {
      error: false,
      success: false,
      email: '',
      pass: '',
      DOToken: '',
      AWSAccessKey: '',
      AWSSecretKey: ''
    };
  }

  handleSubmit (event) {
    event.preventDefault();
    var { router } = this.context;
    var nextPath = router.getCurrentQuery().nextPath;
    var email = this.refs.email.getValue();
    var pass = this.refs.pass.getValue();
    var DOToken = this.refs.DOToken.getValue();
    var AWSAccessKey = this.refs.AWSAccessKey.getValue();
    var AWSSecretKey = this.refs.AWSSecretKey.getValue();
    auth.register(email, pass, DOToken, AWSAccessKey, AWSSecretKey, (registration) => {
      console.log(registration);
      if (registration.successfull) {
        this.setState({
            error: false,
            success: true
          }
        );
      } else {
        this.setState({
            error: true,
            success: false
          }
        );
      }
    });
  }

  render () {
    let component = this;
    let inputs = [
      <Input type='text' label='Email' labelClassName='col-xs-offset-3 col-xs-1' value={component.state.email} wrapperClassName='col-xs-2' ref={'email'} onChange={component.handleChange} bsStyle={component.validateEmail()}/>,
      <Input type='password' label='Password' labelClassName='col-xs-offset-3 col-xs-1' value={component.state.pass} wrapperClassName='col-xs-2' ref={'pass'} onChange={component.handleChange} bsStyle={component.validatePassword()}/>,
      <Input type='text' label='DOToken' labelClassName='col-xs-offset-3 col-xs-1' valie={component.state.DOToken} wrapperClassName='col-xs-2' ref={'DOToken'} onChange={component.handleChange} bsStyle={component.validateToken("DOToken")}/>,
      <Input type='text' label='AWSAccessKey' labelClassName='col-xs-offset-3 col-xs-1' wrapperClassName='col-xs-2' ref={'AWSAccessKey'} onChange={component.handleChange} bsStyle={component.validateToken("AWSAccessKey")}/>,
      <Input type='text' label='AWSSecretKey' labelClassName='col-xs-offset-3 col-xs-1' wrapperClassName='col-xs-2' ref={'AWSSecretKey'} onChange={component.handleChange} bsStyle={component.validateToken("AWSSecretKey")}/>
    ];

    let rows = inputs.map((item) => {
      return <Row>{item}</Row>;
    });

    return (
    <form onSubmit={this.handleSubmit}>
      {rows}
      <Row/>
      <Row>
        <Input wrapperClassName='col-xs-offset-4 col-xs-1' type='submit' value='Register'
          help={this.state.success?'Registration succesfull.':'Wrong informations.'}/>
      </Row>
    </form>
    );
  }
}

Signup.contextTypes = {
  router: React.PropTypes.func
};

module.exports = Signup;
