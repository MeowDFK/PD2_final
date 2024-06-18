import { FormLayout } from '@hilla/react-components/FormLayout.js';
import UserRecordModel from 'Frontend/generated/com/example/application/data/service/UserService/UserRecordModel';
import { TextField, TextFieldChangeEvent } from '@hilla/react-components/TextField.js';
import { PasswordField, PasswordFieldChangeEvent, PasswordFieldElement } from '@hilla/react-components/PasswordField.js';
import React, { useEffect, useState } from 'react';
import { Button } from '@hilla/react-components/Button.js';
import { useNavigate } from 'react-router-dom';
import { TabSheet } from "@hilla/react-components/TabSheet.js";
import { Tab } from '@hilla/react-components/Tab.js';
import { Tabs } from '@hilla/react-components/Tabs.js';
import { useForm } from '@hilla/react-form';
import { UserEndPoint } from 'Frontend/generated/endpoints';
import UserRecord from 'Frontend/generated/com/example/application/data/service/UserService/UserRecord';
import { EndpointValidationError, login } from '@hilla/frontend';
import { EndpointError } from '@hilla/frontend';
import { Dialog } from '@hilla/react-components/Dialog.js';
import { Notification } from '@hilla/react-components/Notification.js';

export function LoginView() {
 
  const [account, setAccount] = useState('');
  const [password, setPassword] = useState('');
  const [submitting , setSubmitting] = useState(false);
  const {model ,submit ,field ,invalid ,addValidator} = useForm(UserRecordModel,{
    
      onSubmit: async (e) => {
        setSubmitting(true);
        try{
          await UserEndPoint.register(e);
          registSeccess();
          }
        catch (error: any) {
          if (error instanceof EndpointValidationError) {
            (error as EndpointValidationError).validationErrorData.forEach(
              ({ parameterName, message }) => {
                console.warn(message); 
              }
            );
          }
          if (error instanceof EndpointError) {
            console.warn((error as EndpointError).message); // "Not implemented"
            console.warn((error as EndpointError).type); // "dev.hilla.exception.EndpointException"
            showError((error as EndpointError).message+"try again");
          }
          
        }
        finally {
          setSubmitting(false);
        }
      }
    });
    useEffect(() => {
      addValidator({
        message: 'Please check that the password is repeated correctly',
        validate: (value: UserRecord) => {
          if (value.password != value.repeatPassword) {
            return [{ property: model.password }];
          }
          return [];
        }
      });
    }, []);
    const responsiveSteps = [
        { minWidth: '100px', columns: 1 },
      ];
    
      const navigate = useNavigate();
    
       

        const LoginClick = async () => {
            try{
              await UserEndPoint.login(account, password)
              const currentAccount : string = await UserEndPoint.getCurrentUser();
              navigate(`/chatList`);
            }catch (error:any){
              if (error instanceof EndpointValidationError) {
                (error as EndpointValidationError).validationErrorData.forEach(
                  ({ parameterName, message }) => {
                    showError(message+"please login again"); 
                    console.warn("please login again");
                  }
                );
              }
              if (error instanceof EndpointError) {
                console.warn((error as EndpointError).message); // "Not implemented"
                console.warn((error as EndpointError).type); // "dev.hilla.exception.EndpointException"
                showError("wrong please login again");
              }

            }
  
          };
      function registSeccess(){
          const notification = Notification.show("successful regist try login now!!", {
            position: 'middle',
            duration: 500,
          });
          setTimeout(() => {
            setSubmitting(false);
        }, 1000); 
          
          
          return <></>;
      }
      

      function showError(error: string) {
            const notification = Notification.show(error, {
              position: 'middle',
              duration: 500,
            });
            
            setTimeout(() => {
              setSubmitting(false);
          }, 1000); 
            
            return <></>;
          }
          
    function handleAccount(e: TextFieldChangeEvent){
      setAccount(e.target.value);
    }
    function handlePassword(e: PasswordFieldChangeEvent){
      setPassword(e.target.value);
    }
    

    return(

    <TabSheet>
        <Tabs slot="tabs">
          <Tab id="login-tab">Login</Tab>
          <Tab id="register-tab">Register</Tab>
        </Tabs>

      <div {...{ tab: 'register-tab' }}>
      <div className="flex flex-col h-full items-center justify-center p-l text-center box-border">
    <h2 className="mb-4">Register</h2>
    <div className="flex flex-col items-center">
      <FormLayout responsiveSteps={responsiveSteps}>
          <TextField label="Username" {...field(model.username)}  />
          <TextField {...{ colspan: 2 }} label="Account" {...field(model.account)}  />
          <PasswordField label="Password" {...field(model.password)}/>
          <PasswordField label="Confirm password" {...field(model.repeatPassword)} />
        </FormLayout>
        <div style={{ marginTop: '20px', fontSize: '14px' }}><Button onClick={submit} disabled={submitting}>Save</Button>
          <span className="label" style={{visibility: submitting ? 'visible' : 'hidden' }}>submitting</span>
          <div className="spinner" style={{visibility: submitting ? 'visible' : 'hidden' }}></div>
    </div>
    </div>
    
        </div>
        </div>

        <div {...{ tab: 'login-tab' }}>
        <div className="flex flex-col h-full items-center justify-center p-l text-center box-border">
      <h2 className="mb-4">Login</h2>
      <div className="flex flex-col items-center">
        <FormLayout responsiveSteps={responsiveSteps}>
          <TextField {...{ colspan: 2 }} label="Account" onChange={handleAccount} required/>
          <PasswordField label="Password" onChange={handlePassword}  required />
        </FormLayout>

        <div style={{ marginTop: '25px', fontSize: '14px', display: 'flex', justifyContent: 'center', gap: '10px' ,}}>
        <div style={{ marginTop: '20px', fontSize: '14px' }}><Button onClick={LoginClick}disabled={submitting}>Login</Button></div>
        <span className="label" style={{visibility: submitting ? 'visible' : 'hidden' }}>submitting</span>
          <div className="spinner" style={{visibility: submitting ? 'visible' : 'hidden' }}></div>
      </div>   
    </div>
    </div>
    
        </div>
      
    </TabSheet>
    );
}