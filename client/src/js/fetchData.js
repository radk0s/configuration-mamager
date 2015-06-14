const request = require('superagent');
const Promise = require('bluebird');
const auth = require('./Auth.js');


module.exports =
  function fetchData(path, method, filters) {
    return new Promise((resolve, reject) => {
      request[method](`${path}`)
        .set('authToken', auth.getToken())
        .set('Accept', 'application/json')
        .send(filters)
        .end((err, res) => {
          const result =  res && res.body || {};
          console.log(result.id == 'not_found');
          if (result.id == 'not_found') reject(result);
          if (err) reject(err.message);
          if (res) resolve(res.body);
        })
    });

  };

//if ( !_.isEmpty(res) && !_.isEmpty(res.body) && !_.isEmpty(res.body.droplets) )

