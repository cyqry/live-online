import React, { useState } from 'react';
import Grid from '@mui/material/Grid';
import Chip from '@mui/material/Chip';
import { Box, Button, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import useStyles from './styles';
import { useEffect } from 'react';
import { getSecondLevels } from '../../../../Api/spaceApi';


const CategoryGrid = ({ onMouseEnter, onMouseLeave, detailsClassName, width = "300px" }) => {
    let classes = useStyles();
    const [categories, setCategories] = useState([]);



    useEffect(() => {
        getSecondLevels().then(
            (res) => {
                setCategories(res)
            },
            (e) => {
            }
        ).catch(e => {
        })
    }, [])
    const length = categories.length;
    const splitIndex = Math.floor(length * 0.8);

    const recommendList = categories.slice(0, splitIndex);
    const otherList = categories.slice(splitIndex);

    return (
        <Box
            onMouseEnter={onMouseEnter}
            onMouseLeave={onMouseLeave}
            className={classes.categoryGrid + " " + detailsClassName}
            width={{ width }}
        >
            <Typography component={'b'} style={{ display: "inline-block", paddingLeft: "5%", marginTop: "9px" }}>
                热门推荐
            </Typography>
            <Grid container spacing={1} style={{ padding: '1rem' }}>
                {recommendList.map((category, index) => (
                    <Grid item key={index} md={((category.name.length * 12) + 40) * 12 / 268}>
                        <Link to={`/live?s=${category.id}&sName=${category.name}`} >
                            <Chip label={category.name} sx={{
                                cursor: "pointer",
                                "&:hover": {
                                    backgroundColor: "orange",
                                    color:"white"
                                }
                            }} />
                        </Link>
                    </Grid>
                ))}
            </Grid>
            <Typography component={'b'} style={{ display: "inline-block", paddingLeft: "5%", marginTop: "2px" }}>
                其他推荐
            </Typography>
            <Grid container spacing={1} style={{ padding: '1rem', width: "300px" }}>
                {otherList.map((category, index) => (
                    <Grid item key={index} md={((category.name.length * 12) + 40) * 12 / 268}>
                        <Link to={`/live?s=${category.id}&sName=${category.name}`} >
                            <Chip label={category.name} sx={{
                                cursor: "pointer",
                                "&:hover": {
                                    backgroundColor: "orange",
                                    color:"white"
                                }
                            }} />
                        </Link>
                    </Grid>
                ))}
            </Grid>
            <div style={{ textAlign: "center", marginTop: "20px" }}>
                <Button
                    className={classes.more}
                    variant="contained"
                    component={Link}
                    to="/class"
                    state={{}}
                >
                    {'更多 >'}
                </Button>
            </div>

        </Box >

    );
};

export default CategoryGrid;
