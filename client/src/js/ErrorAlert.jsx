const React = require('react');
const {Alert, Button} = require('react-bootstrap');

module.exports = React.createClass({
  getInitialState() {
    return {
      alertVisible: true
    };
  },
  componentWillReceiveProps(nextProps) {
    this.setState({alertVisible: true})
  },
  handleAlertDismiss() {
    this.setState({alertVisible: false});
  },
  render() {
    if (this.state.alertVisible) {
      return (
        <Alert bsStyle={this.props.type  || 'danger'} onDismiss={this.handleAlertDismiss}>
          <h4>Provider Error: {this.props.error.message}</h4>
          <p>{JSON.stringify(this.props.error)}</p>
        </Alert>
      );
    }
    return <span></span>;
  }
});
