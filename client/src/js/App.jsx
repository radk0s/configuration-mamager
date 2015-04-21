const React = require('react');
const Router = require('react-router');
const { Route, RouteHandler, Link, DefaultRoute} = Router;
const auth = require('./Auth.js');
const Login = require('./Login.jsx');
const Logout = require('./Logout.jsx');
const About = require('./About.jsx');
const Dashboard = require('./Dashboard.jsx');
const Signup = require('./Signup.jsx');
const Machines = require('./Machines.jsx');
const {Navbar, Nav, NavItem } = require('react-bootstrap');
const NavItemLink = require('react-router-bootstrap').NavItemLink;

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
      <Navbar brand="VMs Manager" inverse>
        <Nav navbar bsStyle="pills" activeKey={1}>
          {this.state.loggedIn ? (<NavItemLink to="dashboard">Dashboard</NavItemLink>):''}
          {this.state.loggedIn ? (<NavItemLink to="machines">Machines</NavItemLink>):''}
          <NavItemLink to="about">About</NavItemLink>
        </Nav>
        <Nav navbar right bsStyle="pills" activeKey={1}>
          {this.state.loggedIn ? (
            <NavItemLink to="logout">Log out</NavItemLink>
          ) : (
            <NavItemLink to="login">Sign in</NavItemLink>
          )}
          {this.state.loggedIn ?'':(<NavItemLink to="signup">Sign up</NavItemLink>)}
        </Nav>
      </Navbar>
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
    <Route name="signup" handler={Signup}/>
    <Route name="dashboard" handler={Dashboard}/>
    <Route name="machines" handler={Machines}/>
    <DefaultRoute name="default" handler={Login}/>
  </Route>
);

Router.run(routes, function (Handler) {
  React.render(<Handler/>, document.getElementById('main'));
});
