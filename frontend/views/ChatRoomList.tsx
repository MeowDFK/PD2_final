import React, { useEffect, useState } from 'react';
import { ChatEndPoint } from 'Frontend/generated/endpoints'; // Import the generated endpoints
import { HorizontalLayout } from '@hilla/react-components/HorizontalLayout.js';
import { VerticalLayout } from '@hilla/react-components/VerticalLayout.js';
import type User from 'Frontend/generated/com/example/application/data/entity/User';
import type ChatRoom from 'Frontend/generated/com/example/application/data/entity/ChatRoom'; // Import the generated ChatRoom type
import { TextFieldChangeEvent } from '@vaadin/text-field/src/vaadin-text-field.js';
import { TextField } from '@hilla/react-components/TextField.js';
import { Button } from '@hilla/react-components/Button.js';
import { Avatar } from '@hilla/react-components/Avatar.js';
import { useNavigate } from 'react-router-dom';
import { Icon } from '@hilla/react-components/Icon.js';
import { useParams } from 'react-router-dom';
import UserRecordModel from 'Frontend/generated/com/example/application/data/service/UserService/UserRecordModel';
import RoomRecordModel from 'Frontend/generated/com/example/application/data/endpoints/ChatEndPoint/RoomRecordModel';
import RoomRecord from 'Frontend/generated/com/example/application/data/endpoints/ChatEndPoint/RoomRecord';
import UserRecord from 'Frontend/generated/com/example/application/data/service/UserService/UserRecord';
import { UserEndPoint } from 'Frontend/generated/endpoints';
import { EndpointValidationError } from '@hilla/frontend';
import { Dialog } from '@hilla/react-components/Dialog.js';
const avatarStyle = {
  height: '64px',
  width: '64px',
};

type ChatRoomWithUser = {
  chatRoom: ChatRoom;
  otherUser: User;
};

export function ChatRoomList() {
  const [account,setAccount] = useState('');
  const navigate = useNavigate();
  const [chatRoomsWithUsers, setChatRoomsWithUsers] = useState<ChatRoomWithUser[]>([]);
  const [input, setInput] = useState('');

  useEffect(() => {
    
      fetchUserAndChatRooms();
  
  }, []);
  async function checkUrl (account: string){
    try {
    const  cur = await UserEndPoint.getCurrentUser();
      if(account!=cur)setDialogOpened(true);
    }catch (error) {
      if (error instanceof EndpointValidationError) {
        (error as EndpointValidationError).validationErrorData.forEach(
          ({ parameterName, message }) => {
            console.warn(message); 
          }
        );
        setDialogOpened(true);
      }
    }
  }
  async function fetchUserAndChatRooms() {
    try {
      const account = await UserEndPoint.getCurrentUser();
        if(account){
          setAccount(account);
          const rooms: RoomRecord[] = await ChatEndPoint.getUserChatRooms(account);
          const roomsWithUsers = await Promise.all(
            rooms.map(async (room) => {
              if (room.id !== undefined) {
                
                const otherUser: UserRecord = await ChatEndPoint.getOtherUserInChatRoom(room.id, account);
                return { chatRoom: room, otherUser };
              }
              return null;
            })
          );
          setChatRoomsWithUsers(roomsWithUsers.filter(room => room !== null) as ChatRoomWithUser[]);
      }
      else  setDialogOpened(true);
    } catch (error: any) {
      if (error instanceof EndpointValidationError) {
        (error as EndpointValidationError).validationErrorData.forEach(
          ({ parameterName, message }) => {
            console.warn(message); 
          }
        );
      }
       navigate("/");
    }
  
  }

  function handleInput(e: TextFieldChangeEvent) {
    setInput(e.target.value);
  }

  function handleDialog(){
    setDialogOpened(false);
    navigate('/');
  }

  function navRoom  (roomID: number,account:  string) {
    const url = `/chat/${roomID}/${account}`;
    navigate(url);
    //ChatEndPoint.enterRoom(roomID, account);
  };
  const [dialogOpened, setDialogOpened] = useState(false);
 
  return (
      <div>
          <div>

          <Dialog
              headerTitle="Login Status"
              opened={dialogOpened}
              onOpenedChanged={({ detail }) => setDialogOpened(detail.value)}
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
        <h2>Chat Rooms</h2>
        {chatRoomsWithUsers.map(({ chatRoom, otherUser }) => {
          const chatRoomId = chatRoom.id !== undefined ? chatRoom.id : 0; // Default value 0 if id is undefined
          return (
            <HorizontalLayout theme="spacing margin" key={chatRoom.id}>
              <Avatar
                name={otherUser.username}
                style={avatarStyle}
              />
              <VerticalLayout>
                <b>{otherUser.username}</b>
                <span>{otherUser.mbti}</span>
                <Button autofocus theme="Secondary"  onClick={() => navRoom(chatRoomId,account)}>
                  Chat
                 
                </Button>
              </VerticalLayout>
            </HorizontalLayout>
          );
        })}
      </div>
    );
}


