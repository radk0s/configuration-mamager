const React = require('react');
const auth = require('./Auth.js');
const {Grid, Row, Col, Input, Button} = require('react-bootstrap');

class Signup extends React.Component {

  constructor () {
    this.handleSubmit = this.handleSubmit.bind(this);
    this.state = {
      error: false,
      success: false
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
    return (
    <form onSubmit={this.handleSubmit}>
      <Row>
        <Input type='text' label='Email' labelClassName='col-xs-offset-3 col-xs-1' wrapperClassName='col-xs-2' ref={'email'}/>
      </Row>
      <Row>
        <Input type='password' label='Password' labelClassName='col-xs-offset-3 col-xs-1' wrapperClassName='col-xs-2' ref={'pass'}/>
      </Row>
      <Row>
        <Input type='text' label='DOToken' labelClassName='col-xs-offset-3 col-xs-1' wrapperClassName='col-xs-2' ref={'DOToken'}/>
      </Row>
      <Row>
        <Input type='text' label='AWSAccessKey' labelClassName='col-xs-offset-3 col-xs-1' wrapperClassName='col-xs-2' ref={'AWSAccessKey'}/>
      </Row>
      <Row>
        <Input type='text' label='AWSSecretKey' labelClassName='col-xs-offset-3 col-xs-1' wrapperClassName='col-xs-2' ref={'AWSSecretKey'}/>
      </Row>
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
