import React from 'react';
import SearchBar from './SearchBar';
import LiveSearchResults from './LiveSearchResults';
import useStyles from './style';
import { useLocation, useNavigate } from 'react-router';
import { useSearchParams } from 'react-router-dom';
import { searchRelativeAnchor } from '../../Api/userApi';
import { useNotification } from '../NotificationProvider';
import { getDataFromErrorOrDefault } from '../../Common/util';
import { useState } from 'react';
import { searchRelativeRoomBase } from '../../Api/spaceApi';
import { useEffect } from 'react';

const Search = () => {
    const [searchResults, setSearchResults] = useState([]);
    const { show } = useNotification();
    let [params, setParams] = useSearchParams()
    let classes = useStyles();

    const nav = useNavigate()

    useEffect(() => {
        let searchValue = params.get('target')

        searchRelativeRoomBase(searchValue).then(
            (rooms) => {
                rooms = rooms.map(r => { return { ...r, type: 1 } })
                searchRelativeAnchor(searchValue).then(
                    (anchors) => {
                        anchors = anchors.map(r => { return { ...r, type: 2 } })
                        setSearchResults((s) => [...rooms, ...anchors])
                    },
                    (e) => {
                        setSearchResults([])
                        show(getDataFromErrorOrDefault(e, "网络错误!", "error", 200))
                    }
                )
            },
            (e) => {
                show(getDataFromErrorOrDefault(e, "网络错误!", "error", 200))
            }
        )

    }, [params.get('target')])

    const handleSearch = (searchValue) => {
        // 这里你可以调用API或者其他方法来获取搜索结果
        // 在这个示例中，我们将使用静态数据模拟
        if (!searchValue || searchValue == '') {
            return
        }

        nav("/search?target=" + searchValue, {})
    };



    return (
        <div className={classes.search}>
            <header className={classes.header}>
                <SearchBar onSearch={handleSearch} value={params.get('target')} />
            </header>
            <main>
                <LiveSearchResults condition={params.get('target')} searchResults={searchResults} />
            </main>
        </div>
    );
};

export default Search;