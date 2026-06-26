import React from 'react';
import ReactDOM from 'react-dom';
import Test from "./component/z_Test";
import Header from "./component/Header";
import Main from "./component/Main";
import adapter from "webrtc-adapter";
// const root = ReactDOM.createRoot(document.getElementById('root'));
// root.render(
//     <Main/>
// );

ReactDOM.render(<Main />, document.getElementById("root"))