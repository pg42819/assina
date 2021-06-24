import React, { Component, useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import './Download.css';
import { sign, createCredential } from '../../util/APIUtils';
import axios from 'axios';
import Alert from 'react-s-alert';
import {ACCESS_TOKEN, API_BASE_URL, CSC_BASE_URL} from '../../constants';


class Download extends Component {
    constructor(props) {
        super(props);
        console.log(props);
        this.state = {fileLink: ''};
        
    }

    render() {

        return (
            <div className="download-container">
                <div className="container">
                    <a href={this.props.location.state.fileLink}>Download signed pdf</a>
                </div>
            </div>
        );
    }
}

export default Download
