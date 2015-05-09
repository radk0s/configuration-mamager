const React = require('react');
const auth = require('./Auth.js');
const request = require('superagent');
const {Grid, Row, Col} = require('react-bootstrap');


let Dashboard = React.createClass({
    getInitialState() {
      return {
        userToken: "",
        userEmail: "",
        userDOToken: "",
        userAWSAccessKey: "",
        userAWSSecretKey: ""
      }
    },
    componentDidMount() {
      console.log(auth.getToken());
      request
        .get('/hello')
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .end((err, res) => {
          console.log(res.body);
          this.setState(res.body);
        });
    },
    render() {
      var token = auth.getToken();
      console.log(this);
      return (
      <Grid>
        <Row className='show-grid'>
          <Col xs={6} xsOffset={2}><p>User Email: {this.state.userEmail}</p></Col>
        </Row>
        <Row className='show-grid'>
          <Col xs={8} xsOffset={2}><p>DO Token: {this.state.userDOToken}</p></Col>
        </Row>
        <Row className='show-grid'>
          <Col xs={6} xsOffset={2}><p>AWS Access Key: {this.state.userAWSAccessKey}</p></Col>
        </Row>
        <Row className='show-grid'>
          <Col xs={6} xsOffset={2}><p>AWS Secret Key: {this.state.userAWSSecretKey}</p></Col>
        </Row>
      </Grid>
      );
    }
  });

module.exports = Dashboard;
