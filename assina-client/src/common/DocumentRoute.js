import React from 'react';
import {
    Route,
    Redirect
  } from "react-router-dom";
  
  
const DocumentRoute = ({ component: Component, authenticated, ...rest }) => (
    <Route
      {...rest}
      render={props =>
        authenticated ? (
          <Component {...rest} {...props} />
        ) : (
          <Redirect
            to={{
              pathname: '/sign',
              state: { from: props.location }
            }}
          />
        )
      }
    />
);
  
export default DocumentRoute