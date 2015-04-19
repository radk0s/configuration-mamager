const React = require('react');
const auth = require('./Auth.js');

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
    var email = this.refs.email.getDOMNode().value;
    var pass = this.refs.pass.getDOMNode().value;
    var DOToken = this.refs.DOToken.getDOMNode().value;
    var AWSToken = this.refs.AWSToken.getDOMNode().value;
    auth.register(email, pass, DOToken, AWSToken, (registration) => {
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
        <label><input ref="email" placeholder="email"/></label>
        <label><input type="password" ref="pass" placeholder="password"/></label>
        <label><input ref="DOToken" placeholder="DOToken"/></label>
        <label><input ref="AWSToken" placeholder="AWSToken"/></label>
        <button type="submit">Register</button>
        {this.state.error && (<p>Wrong informations.</p>)}
        {this.state.success && (<p>Registration succesfull.</p>)}
      </form>
    );
  }
}

Signup.contextTypes = {
  router: React.PropTypes.func
};

module.exports = Signup;
