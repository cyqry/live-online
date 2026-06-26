import React from 'react';
import { Box, TextField, IconButton, Button } from '@mui/material';
import { Search } from '@mui/icons-material';
import { useRef } from 'react';
import { useState } from 'react';

const SearchBar = ({ onSearch, value }) => {

    const [searchValue, setSearchValue] = useState(value)

    const handleSearch = () => {
        onSearch(searchValue);
    };
    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            onSearch(searchValue);
        }
    }

    return (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', width: "401px", height: "42px", m: 2, borderRadius: "30px" }}>
            <TextField
                value={searchValue}
                onChange={(e) => {
                    setSearchValue(e.target.value)
                }}
                inputProps={{
                    onKeyDown: handleKeyDown
                }}
                placeholder="搜索直播"
                variant="outlined"
                size="small"
                sx={{ flexGrow: 1, backgroundColor: "white" }}
            />

            <Button sx={{ backgroundColor: "orange", borderTopLeftRadius: 0, borderBottomLeftRadius: 0, height: "90%", color: "white" }} onClick={handleSearch}>
                <Search />
                搜索
            </Button>
        </Box>
    );
};

export default SearchBar;
