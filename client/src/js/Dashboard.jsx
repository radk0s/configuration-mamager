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
      let component = this;
      let inputs = [
          <Col xs={6} xsOffset={2}><p>User Email: {component.state.userEmail}</p></Col>,
          <Col xs={8} xsOffset={2}><p>DO Token: {component.state.userDOToken}</p></Col>,
          <Col xs={6} xsOffset={2}><p>AWS Access Key: {component.state.userAWSAccessKey}</p></Col>,
          <Col xs={6} xsOffset={2}><p>AWS Secret Key: {component.state.userAWSSecretKey}</p></Col>
      ];
      let rows = inputs.map((item) => {
        return <Row className='show-grid'>{item}</Row>;
      });
      return (
      <Grid>
        {rows}
      </Grid>
      );
    }
  });

module.exports = Dashboard;
