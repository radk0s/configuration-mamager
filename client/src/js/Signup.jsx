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
    var firstName = this.refs.firstName.getDOMNode().value;
    var lastName = this.refs.lastName.getDOMNode().value;
    auth.register(email, pass, firstName, lastName, (registration) => {
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
        <label><input ref="pass" placeholder="password"/></label>
        <label><input ref="firstName" placeholder="firstName"/></label>
        <label><input ref="lastName" placeholder="lastName"/></label>
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
