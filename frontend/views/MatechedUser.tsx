//import { ViewConfig } from '@hilla/hilla-file-router/types.js';
import { Button } from '@hilla/react-components/Button.js';
import { useState,useEffect } from 'react';
import { UserEndPoint } from 'Frontend/generated/endpoints';
import { Dialog } from '@hilla/react-components/Dialog.js';
import { useNavigate } from 'react-router-dom';
import { ProgressBar } from '@hilla/react-components/ProgressBar.js';
import { ChatEndPoint } from 'Frontend/generated/endpoints';

/*export const config: ViewConfig = {
    menu: { order: 4, icon: 'line-awesome/svg/globe-solid.svg' },
    title: 'User Page',
  };*/
type userData ={
    name: string,
    mbti: string,
    avatarUrl:string,
    tags: string[][]
}
export  function MatchedUserView() {
    const navigate = useNavigate();
    const [account , setAccount] = useState('');
    const [errorOpened , setErrorOpened] =useState(false); 
    const [dialogOpened, setDialogOpened] = useState(true);
    const [dialogContent, setDialogContent] = useState('initial');
    const [cantFind, setCantFind] = useState(false);
    const [visButtom, setVisButtom] = useState(true);
    const [roomID,setRoomID] = useState(0);
    const [user, setUser] = useState('')
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
      const [userData, setUserData] = useState<userData>({
        name: "???",
        mbti: "????",
        avatarUrl: 'images/MBTIHEAD.jpg',
        tags: [['???'], ['???'], ['????']]
    });
    async function findMatch(){
        try{
        const otherUser = await UserEndPoint.findOrCreateMatchForUser(account);
            userData.name = otherUser.username;
            userData.mbti = otherUser.mbti
            userData.tags = [otherUser.sports,otherUser.movies,otherUser.foods]
            userData.avatarUrl = 'images/girl.jpg';
        
        const room  = await ChatEndPoint.createChatRoom(account,otherUser.account);
        if(room.id !== undefined)
            setRoomID(room.id);
        setVisButtom(false);
        setDialogOpened(false);
        }catch(error){
            setCantFind(true);
            setTimeout(() => {
                navigate('/match')
            }, 1000); 
        }
    }
    const handleGoClick = () => {
        findMatch();
        setDialogContent('changed');  
        setTimeout(() => {
        
        }, 5000);  
        
    };

function handleDialog(){
    setErrorOpened(false);
    
        navigate('/')
    
  }
  function handleCancel(){
     setDialogOpened(false);
     setTimeout(() => {
        navigate('/match')
    }, 1000); 
  }
function toGpt(){
    setCantFind(false);
    setTimeout(() => {
        navigate('/aiChat');
    }, 1000); 
}
  function goChat(){
    navigate(`/chat/${roomID}/${account}`);
  }
  return (
    <div style={{ textAlign: 'center' }}>
      <div><img src={userData.avatarUrl} alt="User Avatar" style={{ width: '300px', borderRadius: '50%' }} /></div>
      
      <h1 style={{ border: '2px solid black', padding: '7px', display: 'inline-block' }}>
      {userData.name}
    </h1>
    <div style={{ marginTop: '8px' }}>
  <h2>MBTI: {userData.mbti}</h2>
  </div>
      
      <div>
        <ul style={{ listStyleType: 'none', padding: 0, display: 'flex', justifyContent: 'center' }}>
          {userData.tags.map((tag, index) => (
            <p key={index} style={{ margin: '0 5px' }}>{tag}</p>
          ))}
        </ul>
      </div>
      <div style={{ marginTop: '25px', fontSize: '14px', display: 'flex', justifyContent: 'center', gap: '10px' }}>
      <Button theme = "large" onClick={goChat} disabled = {visButtom} >Chat Now!</Button>
      </div>
      <div>

            <Dialog
                headerTitle="Can't Find Match"
                opened={cantFind}
                onOpenedChanged={({ detail }) => setCantFind(detail.value)}
                footerRenderer={() => (
                    <Button theme="primary" onClick={toGpt}>Go Chat</Button>
                )}
            >
                <div style={{ textAlign: 'center', minHeight: '100px', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column' }}>
                <h2>Let Chat with AI </h2>
            </div>
        </Dialog>
    </div>
    <div>

        <Dialog
            headerTitle="Can't Find Match"
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
    <Dialog
                    headerTitle=""
                    opened={dialogOpened}
                    onOpenedChanged={({ detail }) => {
                    setDialogOpened(detail.value);
                    }}
                    footerRenderer={() => (
                    <>
                    {dialogContent === 'initial' ? (
                    <Button theme="primary" onClick={handleGoClick}>Go</Button>
                    ) : (
                    <Button theme="primary" onClick={handleCancel}>Cancel</Button>
                    )}
                    </>
                )}
                >

                <div style={{ textAlign: 'center', minHeight: '200px', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column' }}>
                {dialogContent === 'initial' ? (
                    <>
                    <h2>Are you Ready to find Someone matched?</h2>
                    <p>Only one time per day</p>
                    <img style={{ width: '200px' }} src="images/only-one.jpg" />
                    </>
                ) : (
                    <div style={{ width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', minHeight: '200px' }}>
                    <h2>Searching...</h2>
                    <p>Wait a second</p>
                    <ProgressBar indeterminate style={{ width: '80%' }} />
                    </div>
                )}
                </div>
                </Dialog>
    </div>
    
  );
}

