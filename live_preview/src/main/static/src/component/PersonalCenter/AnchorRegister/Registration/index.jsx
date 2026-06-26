import { useState } from "react"
import RegistrationEntry from "./RegistrationEntry";
import BecomeStreamer from "./BecomeStreamer";

export default function Registration({ reLoadAnchorRegister }) {
    let [isEnter, setIsEntry] = useState(true);
    return (

        isEnter ? <RegistrationEntry onClick={() => { setIsEntry(false) }} /> : <BecomeStreamer reLoadAnchorRegister={reLoadAnchorRegister} closeRegister={() => { setIsEntry(true) }} />
    )
}