
import { FormLayout } from '@hilla/react-components/FormLayout.js';
import { TextField } from '@hilla/react-components/TextField.js';
import { PasswordField } from '@hilla/react-components/PasswordField.js';
import { Button } from '@hilla/react-components/Button.js';
import { useNavigate } from 'react-router-dom';
import { TabSheet } from "@hilla/react-components/TabSheet.js";
import { Tab } from '@hilla/react-components/Tab.js';
import { Tabs } from '@hilla/react-components/Tabs.js';
import { useState,useEffect } from 'react';
import { Dialog } from '@hilla/react-components/Dialog.js';

import { UserEndPoint } from 'Frontend/generated/endpoints';


export  function MatchView() {
    const [account , setAccount] = useState('');
   
    const [errorOpened , setErrorOpened] =useState(false);  // 狀態變量來管理對話框內容
    const navigate = useNavigate();

    useEffect(() => {
        verify();
      }, []);
      async function verify(){
        try{
          const cur = await UserEndPoint.getCurrentUser();
            if (cur)setAccount(cur);
          }catch{
            setErrorOpened(true);
            
            setTimeout(() => {
                navigate('/')
            }, 1000); 
          }
      }
   
    
    function handleDialog(){
        setErrorOpened(false);
        navigate('/');
      }
   
        return(

               
                <div className="flex flex-col h-full items-center justify-center p-l text-center box-border">
                <img style={{ width: '500px' }} src="images/MBTIHEAD.jpg" />


                <div style={{ marginTop: '25px', fontSize: '14px', display: 'flex', justifyContent: 'center', gap: '10px' }}>
                    <Button theme="large" onClick={()=>navigate('/matchUser')}>Find Someone Matched</Button>
                </div>
                <div>

                <Dialog
                    headerTitle="Login Status"
                    opened={errorOpened}
                    onOpenedChanged={({ detail }) => setErrorOpened(detail.value)}
                    footerRenderer={() => (
                        <Button theme="primary" onClick={handleDialog}>Close</Button>
                    )}
                    >
                    <div style={{ textAlign: 'center', minHeight: '100px', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column' }}>
                        <h2>You haven't Login </h2>
                        <h2>Redirect to Rigester Page or enter with bad entry</h2>
                    </div>
                    </Dialog>
                </div>
                </div>
        );
}