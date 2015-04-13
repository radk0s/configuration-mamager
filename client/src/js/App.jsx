const React = require('react');
const Router = require('react-router');
const { Route, RouteHandler, Link } = Router;
const auth = require('./Auth.js');
const Login = require('./Login.jsx');
const Logout = require('./Logout.jsx');
const About = require('./About.jsx');
const Dashboard = require('./Dashboard.jsx');
const Signup = require('./Signup.jsx');

class App extends React.Component {
  constructor () {
    this.state = {
      loggedIn: auth.loggedIn()
    };
  }

  setStateOnAuth (loggedIn) {
    this.setState({
      loggedIn: loggedIn
    });
  }

  componentWillMount () {
    auth.onChange = this.setStateOnAuth.bind(this);
    auth.login();
  }

  render () {
    return (
      <div>
        <ul>
          <li>
            {this.state.loggedIn ? (
              <Link to="logout">Log out</Link>
            ) : (
              <Link to="login">Sign in</Link>
            )}
          </li>
          <li><Link to="about">About</Link></li>
          <li>
            {this.state.loggedIn ? (
              <Link to="dashboard">Dashboard</Link>):
              (<Link to="signup">Sign up</Link>)
            }
          </li>
        </ul>
        <RouteHandler/>
      </div>
    );
  }
}

var routes = (
  <Route handler={App}>
    <Route name="login" handler={Login}/>
    <Route name="logout" handler={Logout}/>
    <Route name="about" handler={About}/>
    <Route name="dashboard" handler={Dashboard}/>
    <Route name="signup" handler={Signup}/>
  </Route>
);

Router.run(routes, function (Handler) {
  React.render(<Handler/>, document.getElementById('main'));
});
