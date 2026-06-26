import React, { createContext } from 'react'

let { Provider, Consumer } = createContext()

function Middle() {
    return (
        <div>
            这里是中间者<br />
            <CosumTest />
        </div>
    )
}

function CosumTest() {
    return (
        <div>
            这里是消费者: <br />

            {/* 这里也可以不用Consumer， 但是需要createContext()的时候不要 解构赋值 直接 声明 contenxt 变量。这里就可以使用 let value= useContext(context) 拿到上层context.Provider的value了    */}
            <Consumer>
                {/* js函数这种写法，表达式才会有返回值，'=>'后面用{}括起来的话就没有返回值了 */}
                {value =>
                    // 可以发现，Consumer跨越了中间组件从父组件即Provider处拿到了值
                    //   需要在Provider下才能拿到值 
                    <span>值： {value.name}</span>
                }
            </Consumer>
        </div>
    )
}

export default function TestConText() {
    return (
        <Provider value={{name:"wqer",age:23}}>
            <Middle />
        </Provider>
    )
}
