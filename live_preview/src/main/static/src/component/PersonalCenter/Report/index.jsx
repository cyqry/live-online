import React from 'react';
import { useState } from 'react';
import { useEffect } from 'react';
import { getReports } from '../../../Api/spaceApi';
import ReportInfo from './ReportInfo';

const Report = () => {
    // const reportInfos = [
    //     {
    //         roomId: 1,
    //         avatar: 'avatar-url-1',
    //         nickname: 'Anchor 1',
    //         title: 'Room 1',
    //         reports: [
    //             { id: 1, text: 'Report 1', reporter: 'Reporter 1' },
    //             { id: 2, text: 'Report 2', reporter: 'Reporter 2' },
    //         ],
    //     },
    //     {
    //         roomId: 2,
    //         avatar: 'avatar-url-2',
    //         nickname: 'Anchor 2',
    //         title: 'Room 2',
    //         reports: [
    //             { id: 3, text: 'Report 3', reporter: 'Reporter 3' },
    //             { id: 4, text: 'Report 4', reporter: 'Reporter 4' },
    //         ],
    //     },
    // ];

    let [reportInfos, setReportInfos] = useState([]);
    useEffect(() => {
        getReports().then(res => {
            setReportInfos(res)
        })
    }, [])

    return (
        <div>
            {
                reportInfos.length == 0 ? <div style={{ width: "100%", height: "200px", lineHeight: "200px", textAlign: "center" }}>
                    还没有任何举报记录~
                </div> :
                    reportInfos.map((message) => (
                        <ReportInfo
                            key={message.roomId}
                            roomId={message.roomId}
                            avatar={message.avatar}
                            anchorNickname={message.anchorNickname}
                            reports={message.reports}
                        />
                    ))}
        </div>
    );
};

export default Report;