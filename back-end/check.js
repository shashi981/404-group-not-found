async function UIDcheck(con, UID){
    const checkquery = 'SELECT * FROM USERS WHERE UID=?';
    const [userResults] = await con.promise().query(checkquery, [UID]);
  
    if (userResults.length === 0) {
      console.log('Value not found, nothing deleted');
      throw new Error('Value not found');
    }
  }
  
  async function Emailcheck(con, Email){
    const checkquery = 'SELECT * FROM USERS WHERE Email=?';
    const [userResults] = await con.promise().query(checkquery, [Email]);
  
    if (userResults.length === 0) {
      console.log('Value not found, nothing deleted');
      throw new Error('Value not found');
    }
  }
  
  async function ItemIDcheck(con, UID, ItemID){
    const checkquery = 'SELECT ItemID FROM OWNS WHERE UID=? AND ItemID IN (?)';
    const [userResults] = await con.promise().query(checkquery, [UID, ItemID]);
  
    if (userResults.length === 0) {
      console.log('Value not found, nothing deleted');
      throw new Error('Value not found');
    }
  }

  module.exports = { Emailcheck, UIDcheck , ItemIDcheck};

