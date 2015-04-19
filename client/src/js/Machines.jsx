const React = require('react');
const auth = require("./Auth.js");
const request = require('superagent');


let Machines = React.createClass({
    getInitialState() {
        return {
            droplets: []
        }
    },
    componentDidMount() {
        request
            .get('/instances/do')
            .set('X-Auth-Token', auth.getToken())
            .set('Accept', 'application/json')
            .end((err, res) => {
                if (typeof res != "undefined" || res != null) {
                    this.setState(res.body);
                }
            });
    },

    render() {

        if (typeof this.state.droplets == "undefined" || this.state.droplets == null || this.state.droplets.length == 0) {
            return <p>No machines found for you.</p>
        }

        var listItems = this.state.droplets.map(function(item) {
            return <li>
                <h3>{item.name}</h3>
                <p>STATUS : {item.status}</p>
                <p>MEMORY : {item.memory}</p>
                <p>CREATED_AT : {item.created_at}</p>
            </li>;
        });
        return <div><ul>{listItems}</ul></div>;
    }
});


module.exports = Machines;