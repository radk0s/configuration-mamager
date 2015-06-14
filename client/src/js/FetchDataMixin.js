const React = require('react');
const  Alert = require('./ErrorAlert.jsx');
const _fetchData = require('./fetchData');
const _ = require('underscore');

module.exports = {
  fetch(path, method, filters, destinationStateProperty, dataTransformer, cb) {
    _fetchData(path, method, filters)
      .then((data) => {
        let resultData = data;
        if (_.isFunction(dataTransformer)) {
          resultData = dataTransformer(data);
        }
        if(destinationStateProperty){
          this.setState({
            [destinationStateProperty]: resultData,
            [destinationStateProperty+'Loaded']: true
          },() => {
            if (cb) cb();
          });
        } else {
          if (cb) cb();
        }
      })
      .catch((error) => {
        console.log(error);
        React.render(<Alert error={error}/>, document.getElementById('alert'));
      })
      .done();
  }
}

