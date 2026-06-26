import React from 'react';
import { Box, Container, Tab, Tabs, Typography } from '@mui/material';
import { LiveTv, Videocam, People } from '@mui/icons-material';
import Result from './Result';

const TabPanel = (props) => {
    const { children, value, index } = props;

    return (
        <div role="tabpanel" hidden={value !== index}>
            {value === index && <Box>{children}</Box>}
        </div>
    );
};

const LiveSearchResults = ({ condition, searchResults }) => {
    const [value, setValue] = React.useState(0);
    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    return (
        <Box sx={{ flexGrow: 1, mt: 2 }}>
            <Tabs value={value} onChange={handleChange} centered>
                <Tab icon={<LiveTv />} label="综合" />
                <Tab icon={<Videocam />} label="直播" />
                <Tab icon={<People />} label="主播" />
            </Tabs>
            <TabPanel value={value} index={0}>
                <Result condition={condition} type={0} searchResults={searchResults} />
            </TabPanel>
            <TabPanel value={value} index={1}>
                <Result condition={condition} type={1} searchResults={searchResults} />
            </TabPanel>
            <TabPanel value={value} index={2}>
                <Result condition={condition} type={2} searchResults={searchResults} />
            </TabPanel>
        </Box >
    );
};

export default LiveSearchResults;
