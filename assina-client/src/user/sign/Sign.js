import React, { Component, useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import './Sign.css';
import { sign } from '../../util/APIUtils';
import axios from 'axios';
import Alert from 'react-s-alert';


class Sign extends Component {
    constructor(props) {
        super(props);
        console.log(props);
        this.state = {file: '', msg: '', pin: ''};
    
        this.handleInputChange = this.handleInputChange.bind(this);
    }

	onFileChange = (event) => {
		this.setState({
			file: event.target.files[0]
		});
	}

	uploadFileData = (event) => {
		event.preventDefault();

        const data = new FormData();
        data.append('file', this.state.file);
        data.append('pin', this.state.pin);

        axios.post('http://localhost:8000/sign', data, {

        }).then(res => {
            this.props.history.push("/profile");
        })

	}

    handleInputChange(event) {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName] : inputValue
        });
    }

    render() {

        return (
            <div className="sign-container">
                <div className="container">
                    <div className="sign-info">
                        <div className="profile-avatar">
                            { 
                                (
                                    <div className="text-avatar">
                                        <span>{this.props.currentUser.name && this.props.currentUser.name[0]}</span>
                                    </div>
                                )
                            }
                        </div>
                        <div className="profile-name">
                           <h2>{this.props.currentUser.name}</h2>
                           <p className="profile-email">{this.props.currentUser.email}</p>
                        </div>
                    </div>
                </div> 
                <form>
                <div>
                    <input type="file" name="file" onChange={this.onFileChange}/>
                </div>
                <div>
                    <input type="password" pattern="[0-9]*" name="pin" placeholder="Pin"
                            value={this.state.pin}    onChange={this.handleInputChange} required/>
                </div>
                <div>
                        <button type="button" className="btn" onClick={this.uploadFileData}>Upload</button>
                </div>
                </form>
            </div>
        );
    }
}

export default Sign