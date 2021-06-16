import React, { Component } from 'react';
import { Link, NavLink } from 'react-router-dom';
import './Sign.css';

class Sign extends Component {
    constructor(props) {
        super(props);
        console.log(props);
        this.state = {
            selectedFile: null
        }
    }

	onChangeHandler=event=>{
        this.setState({
          selectedFile: event.target.files[0]
        })

        console.log(this.state.selectedFile);
    }

	handleSubmission() {
		const formData = new FormData();

		formData.append('File', this.state.selectedFile);

        console.log("HEYYY")

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
                <input type="file" name="file" onChange={this.onChangeHandler}/>
                    <div>
                        <button onClick={this.handleSubmission}>Submit</button>
                    </div>
                </div>
                </form>
            </div>
        );
    }
}

export default Sign