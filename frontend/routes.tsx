
import MainLayout from 'Frontend/views/MainLayout';
import { lazy } from 'react';
import { createBrowserRouter, RouteObject } from 'react-router-dom';
import {ChatRoomList} from './views/ChatRoomList';
import {ChatView} from './views/ChatView';
import {LoginView} from './views/LoginRegister';
import {PickLabelView} from './views/PickUserLablel';
import {MatchView} from './views/MatchView';
import {MatchedUserView} from './views/MatechedUser';
import {AIChatView}from './views/AIChatView';
import { StreamingChatView } from './views/StreamingChatView';
const AboutView = lazy(async () => import('Frontend/views/about/AboutView.js'));

export const routes: RouteObject[] = [
  
   
    { 
      element: <MainLayout />,
    handle: { title: 'Hilla CRM' },
    children: [
      { path: '/' ,element: <LoginView />,handle: { title: 'Hello!!' }},
      { path: '/pickLabels' ,element: <PickLabelView/>, handle: { title: 'Label' } },
      { path: '/chatList', element: <ChatRoomList/>, handle: { title: 'ChatList' } },
      { path: '/about', element: <AboutView />, handle: { title: 'About' } },
      { path: '/chat/:roomId/:account', element: <ChatView /> ,handle: { title: 'chatroom' }}, 
      { path: '/match', element: <MatchView />, handle: { title: 'Match' } },
      { path: '/matchUser',element: <MatchedUserView/>,handle: { title: 'Match' }},
      { path: '/aiChat' , element: <StreamingChatView/> ,handle: { title: 'AIChat'}},
      { path: '/divination', element: <AIChatView/>,handle: { title: 'Divination'}}
    ],
  },
];

export default createBrowserRouter(routes);
