import * as React from 'react';
import Stack from '@mui/material/Stack';
import Button from "@mui/material/Button";
import { height, width } from "../../../../Common/util";
import { Link, NavLink } from 'react-router-dom';
import useStyles from './styles';
import { useState } from 'react';

function CategoryButton({ children, onMouseEnter, onMouseLeave, state, to }) {

    let sx = {
        width: width(5),
        height: height(6),
        borderRadius: height(3),
        backgroundColor: '#00BCD4',
        '&:hover': {
            backgroundColor: '#00BCD4',
            opacity: [0.9, 0.8, 0.2],
        },
    }

    return (
        <Button to={to} state={state} LinkComponent={Link} onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave} sx={sx} variant={"contained"} size={"medium"}>{children}</Button>
    )

}
export default function HeaderButton({ to, state, children, details: Details }) {
    let classes = useStyles();
    const [inDetails, setInDetails] = useState(false);
    const [inButton, setInButton] = useState(false);


    const handleButtonMouseEnter = (event) => {
        setInButton(true)
    };

    const handleButtonMouseLeave = () => {
        setInButton(false)
    };
    const handlePopperMouseEnter = (event) => {
        setInDetails(true)
    };

    const handlePopperMouseLeave = () => {
        setInDetails(false)
    };
    return (
        <div className={classes.whole}  >
            {/*                        放这里的话，当鼠标进入，Popover会触发一次这里的离开函数，所以离开函数只有放Popover里 */}
            <CategoryButton to={to} state={state} onMouseEnter={handleButtonMouseEnter} onMouseLeave={handleButtonMouseLeave} >{children}</CategoryButton>
            {
                Details && (
                    <Details
                        detailsClassName={classes.details}
                        onMouseEnter={handlePopperMouseEnter}
                        onMouseLeave={handlePopperMouseLeave}
                    />)
            }
        </div>
    );
}

