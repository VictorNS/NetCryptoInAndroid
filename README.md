# NetCryptoInAndroid
Occasionally I needed check a hash for password in Android and dotNet Core.
The code generation is very simple and uses `System.Security.Cryptography.Rfc2898DeriveBytes` class:
```
byte[] salt;
byte[] bytes;
using (var rfc2898DeriveBytes = new System.Security.Cryptography.Rfc2898DeriveBytes(password, 16, 1000))
{
	salt = rfc2898DeriveBytes.Salt;
	bytes = rfc2898DeriveBytes.GetBytes(32);
}
var inArray = new byte[49];
Buffer.BlockCopy(salt, 0, inArray, 1, 16);
Buffer.BlockCopy(bytes, 0, inArray, 17, 32);

return Convert.ToBase64String(inArray);
```
The parsing is easiest, it’s just:
```
Crypto.VerifyHashedPassword(hashedPassword, password)
```
But we don’t have `System.Web.Helpers.Crypto` class in Android and dotNet Core.

This repository is just a result of playing with `VerifyHashedPassword` method.

BTW in my repository Crypto you can get the C# code and also an example of generating the hash.
