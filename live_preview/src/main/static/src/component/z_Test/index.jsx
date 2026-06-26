import * as React from 'react';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import { NavLink, Outlet, Route, Routes, useInRouterContext, useNavigate, useNavigationType, useParams, useSearchParams } from 'react-router-dom';
import {
  createTheme,
  Paper,
  styled,
  ThemeProvider,
  Typography
} from "@mui/material";
import { blue, orange } from "@mui/material/colors";
import { useRef } from 'react';
import { useState } from 'react';
import { useEffect } from 'react';
import ACtivity from './TestActivity';
import RecommendRoom from '../IndexPage/Body/RecommendRoom';
import Loading from '../Loading';
import { log } from '../../Common/util';

function BasicButtons() {
  let theme = createTheme({
    palette: {
      primary: {
        main: blue[900]
      }
    }
  });

  //这个两层函数，生成了一个我们自己的Button
  let MyBt = styled(Button)({
    border: '6px solid red'
  });

  let test = (e) => {
    let y = { r: 2, i: 23 }
    let x = {
      a: 1,
      ...y
    }

    log(x)
    class P {
      connection
      constructor(connection) {
        this.connection = connection
      }
    }
    let p = new P("werwrew");
    log(p.connection)
  }

  return (
    //这里的theme为自定义样式，ThemeProvider之下就会生效，里面的值优先级高
    <ThemeProvider theme={theme}>
      <Stack spacing={2} direction="row">
        <Button color={"warning"} variant="text">Text</Button>
        <Button color={"error"} variant="contained">Contained</Button>

        {/*这里的primary 被我们自定义的参数覆盖了*/}
        <Button size={"small"} color={"primary"}
          variant="outlined">Outlined</Button>
        {/*sx相当于style的扩展,优先级高，额外做了很多事; p: 2 意思是padding为2倍默认大小  */}
        <MyBt onClick={test} color={"primary"}
          sx={{ color: "blue", p: 2 }}>执行test方法</MyBt>

        {/* 路由5是点击NavLink可以增加active类名，现在路由6不支持了，改为渲染该组件就时执行函数，以函数返回值作为类名来增加上去。函数传参是一个只有isActive字段的对象，所以直接解构赋值即可。只要组件被渲染，当路径匹配上这个NavLink的to时(路径.startwith(to)==true 就行)，该字段为true，其余时间为false。*/}
        {/* 这里 的 to , '/' 开头的话就是绝对路径,如 /test/w 就是 <Test/>这个路由组件的一个子路由的path，/w则什么都不是, 但"./w"，是相对于当前路由组件对应的路径，在这里等价于"/test/w" */}
        <NavLink to="./w" className={({ isActive }) => { console.log(isActive); return "test"; }} >TestNavLink</NavLink>
        {/* 在react6中，要向子路由传递数据，只有通过Link用 search参数，params参数这种等等 */}

        {/* 在Route6中，不在这里直接写子路由了，需要写到父路由里面去，然后这里使用OutLet取一下当前路由组件的子路由（useOutLet()可查看这个子路由） ；貌似是一个路由只能有一个子路由 */}
        <Outlet />
      </Stack>
    </ThemeProvider>
  );
}

export default function Test() {

  // 要接收 Link 传过来的params 类组件好像是用 this.location.props来接收，
  //但函数式组件只有
  let { testParam } = useParams();
  console.log("接收到params：", testParam)

  // search参数的取，setParams这个函数一般不用
  let [params, setParams] = useSearchParams()
  console.log("search参数:", params.get("name"), params.get("thank"));
  if (!params.get("thank")) {
    setParams("name=cjh&thank=you")//这里会重新render这个组件
  }

  //useLocaltion()也可以取search参数，也可以取state参数(就是Link传 类似于post请求的body的参数 )，可以log出来研究一下

  //  let navigate= useNavigate(); //像Navigate组件被渲染一样，这里提供一个函数，用于使用history实现一个手动的路由跳转
  //  navigate("/v",{
  //   // 默认
  //   replace: false,
  //   //可像Link组件一样携带参数
  //   state: {
  //     name:"werwr",
  //     id:"123413",
  //   }
  //  })
  //  navigate(-1)//提供history的功能,这里后退
  //  navigate(1) //前进

  // console.log(useInRouterContext());查看是否被外层路由器（如BrowserRouter）包裹

  console.log(useNavigationType());//输出如何到这个组件来的， POP(刷新，新进该页面都是pop),PUSH,REPLACE


  function TestF() {
    let [list, setList] = React.useState([])
    log("f render");
    console.log(document.cookie);
    let addList = () => {
      setList(oldList =>[...oldList])
    }

    return (
      // 可以发现，Fragment并没有被解析为一个标签，解决了层级太多的问题; 这里就相当于只返回了一个按钮，没有外层的标签； 也可以使用<></>包裹，也不会被解析为一个标签，但是这个不能传参数， 但Fragment可以传key这个参数(传其他的会警告)，其他参数传了也没用。
      <React.Fragment>
        <Button onClick={addList} >测试Fragment</Button>
        {list.map((i, index) => <span key={index}>{i}</span>)}
        {/* 这里会有一个问题，TestF的state值并没有改变，但是依然重新render，而且子组件 TestFC 也没用任何改变，也重新render了，就不对劲；不管，先不解决这个问题 */}
        <TestFC /><br/>
      </React.Fragment>
    )
  }

  function TestFC() {
    log("child render");
    return (
      <>
        <span>child</span>
      </>
    )
  }
  let playerRef =useRef();

  return (
    //该参数为阴影宽度
    <Paper elevation={3}>
      <BasicButtons />

      {/*https://mui.com/material-ui/api/typography/#props*/}
      <Typography variant="h1" component="h2">
        h1. Heading
      </Typography>
      <TestF />
      {/* <HistoryList height={'200px'}/> */}
{/* 
      <PersonalCenter/> */}
      <Loading/>

    </Paper>
    
  )
}


function HandleTest(){
  let [handle,setHandle]= useState();

  const handleClick=()=>{
    console.log("handleClick")
    if(handle){
      handle("father")
    }
  }
  
  return (
    <div>
      <Button onClick={handleClick}>点击</Button>
       <ACtivity/>
    </div>
  )
}

function Sun({setHandle}){
  useEffect(()=>{
    setHandle((e)=>{
      console.log(e)
    })
  },[])
  return(
    <p>sun</p>
  )
}

// const Sun2= forwardRef(({ test }, ref) => {
//     useImperativeHandle(ref, () => ({
//         getData: getData,
//         otherFun: otherFun
//     }))
//     function getData() {
//       console.log(test,"tset")
//     }
//     function otherFun() {
//         console.log('这是其他方法')
//     }
//     return (
//         <div>子组件</div>
//     )
// })

  // 一个小的写法问题:
  // <A>   
  //     <B/>
  //  </A>
  //  上面这种写法，虽然，A的内部能通过 props.children 拿到<B/>,但是A的内部却无法传值给 props.children；
  //  所以这样写:
  //  <A  hhh={(someValue) =>  <B  {...someValue}/>  } />  ,然后A的内部 通过 props.hhh({name:"wer",id="wer" }) 这样的形式获得一个拿到了值的 B 组件,就解决了上诉问题

