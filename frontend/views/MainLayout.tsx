import {AppLayout} from '@hilla/react-components/AppLayout';
import {DrawerToggle} from '@hilla/react-components/DrawerToggle';
import Placeholder from 'Frontend/components/placeholder/Placeholder';
import {useRouteMetadata} from 'Frontend/util/routing';
import {Suspense, useEffect} from 'react';
import {NavLink, Outlet} from 'react-router-dom';
import React from 'react';
import { useState } from 'react';
import { Tabs } from '@hilla/react-components/Tabs.js';
import { Tab } from '@hilla/react-components/Tab.js';
import { Icon } from '@hilla/react-components/Icon.js';
import { UserEndPoint } from 'Frontend/generated/endpoints';
import css from './MainLayout.module.css';
import { Button } from '@hilla/react-components/Button.js';
import { useNavigate } from 'react-router-dom';
import { Notification } from '@hilla/react-components/Notification.js';
const h1Style = {
    fontSize: 'var(--lumo-font-size-l)',
    margin: 0,
  };
  const iconStyle: React.CSSProperties = {
    marginInlineEnd: 'var(--lumo-space-m)',
    padding: 'var(--lumo-space-xs)',
    boxSizing: 'border-box',
  };
export default function MainLayout() {
    const [account , setAccount] = useState('');
    const [validate,setValidate] = useState(true);
    const navigate = useNavigate();
    useEffect(() => {
        verify();
      }, []);
      async function verify(){
        try{
          const cur = await UserEndPoint.getCurrentUser();
            if (cur){
                setAccount(cur);
                setValidate(false);
            }
          }catch{
            
          }
      }
    const currentTitle = useRouteMetadata()?.title ?? 'My App';
    function logout(){
        if(account)
        UserEndPoint.logout();
        const notification = Notification.show("Successful Logout!!", {
            position: 'middle',
            duration: 500,
          });
        navigate('/');
        setValidate(true);
    }
    
    return (
        <AppLayout primarySection="drawer">
            <div slot="drawer" className={css.drawer}>

                <header>
                    <h1 className="text-l m-0">My App</h1>
                    <Tabs slot="drawer" orientation="vertical" onClick={()=>verify()}>
                        <nav>
                            <NavLink to="/" >
                                <Icon icon="hilla:dashboard" style={iconStyle} />
                            Login
                            </NavLink>
                            <NavLink to="/chatList/">
                            Chat
                            </NavLink>
                            <NavLink to="/pickLabels">
                            Pick Label 
                            </NavLink>
                            <NavLink to="/match">
                            Find Match
                            </NavLink>
                            <NavLink to="/divination">
                            Divination
                            </NavLink>
                            <NavLink to="/aiChat">
                            AI chat
                            </NavLink>
                            
                        </nav>
                       
                    </Tabs>
                    
                </header>
                <Button theme="second" disabled={validate} onClick={logout}> LogOut </Button>
            </div>
            
            <DrawerToggle slot="navbar" aria-label="Menu toggle"></DrawerToggle>
            <h2 slot="navbar" className="text-l m-0">
                {currentTitle}
            </h2>

            <Suspense fallback={<Placeholder/>}>
                <Outlet/>
            </Suspense>
            
        </AppLayout>
    );
    
    
}
