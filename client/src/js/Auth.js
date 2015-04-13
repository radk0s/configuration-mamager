const React = require('react');
const request = require('superagent');


module.exports = {
  login (email, pass, cb) {
    cb = arguments[arguments.length - 1];
    if (localStorage.token) {
      if (cb) cb(true);
      this.onChange(true);
      return;
    }
    pretendRequest(email, pass, (res) => {
      if (res.authenticated) {
        localStorage.token = res.token;
        if (cb) cb(true);
        this.onChange(true);
      } else {
        if (cb) cb(false);
        this.onChange(false);
      }
    });
  },
  getToken () {
    return localStorage.token;
  },

  logout(cb) {
    delete localStorage.token;
    if (cb) cb();
    this.onChange(false);
  },

  loggedIn() {
    return !!localStorage.token;
  },

  onChange() {
  },
  requireAuth(Component) {
    let auth = this;
    return class Authenticated extends React.Component {
      static willTransitionTo(transition) {
        if (!auth.loggedIn()) {
          transition.redirect('/login', {}, {'nextPath' : transition.path});
        }
      }
      render () {
        return <Component {...this.props}/>
      }
    }
  },
  register (email, password, firstName, lastName, cb) {

    request.post('/auth/api/signup')
      .send({email:email,
        password: password,
        password2: password,
        firstName: firstName,
        lastName: lastName})
      .set('Accept', 'application/json')
      .end(function(err, res){

        if (res.body.status == "OK") {
          cb({
            successfull: true
          });
        } else {
          cb({
            successfull: false
          });
        }
      });

  }
};

function pretendRequest(email, pass, cb)
{
  request.post('/auth/api/authenticate/userpass')
    .send({ username: email, password: pass })
    .set('Accept', 'application/json')
    .end(function(err, res){
      let result = JSON.stringify(res.body);
      console.log(result);
      console.log(res.body.error);
      if (res.ok && !res.body.errorre) {
        cb({
          authenticated: true,
          token: res.body.token
        });
      } else {
        cb({authenticated: false});
      }
    });
}
