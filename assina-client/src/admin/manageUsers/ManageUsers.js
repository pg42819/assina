import React, { Component, useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import './ManageUsers.css';
import { sign, createCredential } from '../../util/APIUtils';
import axios from 'axios';
import Alert from 'react-s-alert';
import {ACCESS_TOKEN, API_BASE_URL, CSC_BASE_URL} from '../../constants';


class ManageUsers extends Component {
    constructor(props) {
        super(props);
        console.log(props);
        this.state = {users: []};

        const token = localStorage.getItem(ACCESS_TOKEN);

        const headers = {
	        'Authorization': 'Bearer '+token
        };

        axios.get(API_BASE_URL+'/users', {
            headers: headers
        }).then(res => {
            this.setState({
                users: res.data
            })
            console.log(this.state.users)
        }).catch(error => console.log(error));

    }

    render() {

        return (
            <div className="manageUsers-container">
                <div className="container">
                    <ul>
                        {this.state.users.map(user => {
                            return <li key={user.id}>{user.name}</li>
                        })}
                    </ul>
                </div>
            </div>
        );
    }
}

export default ManageUsers
