/************************************************************************ 
 * Copyright PointCheckout, Ltd.
 * 
 */
package com.paymennt.solanaj.program;

import com.paymennt.solanaj.data.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class TokenProgram {

    /**  */
    public static final SolanaPublicKey PROGRAM_ID = new SolanaPublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
    /**
     * 2022 token program
     */
    public static final SolanaPublicKey PROGRAM_ID_2022 = new SolanaPublicKey("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");

    /**  Address of the SPL Associated Token Account program. */
    public static final SolanaPublicKey ASSOCIATED_TOKEN_PROGRAM_ID =
            new SolanaPublicKey("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL");

    /**  */
    public static final SolanaPublicKey SYSVAR_RENT_PUBKEY =
            new SolanaPublicKey("SysvarRent111111111111111111111111111111111");

    /**  */
    private static final int CREATE_METHOD_ID = 0;

    /**  */
    private static final int INITIALIZE_METHOD_ID = 1;

    /**  */
    private static final int TRANSFER_METHOD_ID = 3;

    /**  */
    private static final int CLOSE_ACCOUNT_METHOD_ID = 9;

    /**  */
    private static final int TRANSFER_CHECKED_METHOD_ID = 12;

    /**
     * Transfers an SPL token from the owner's source account to destination account.
     * Destination pubkey must be the Token Account (created by token mint), and not the main SOL address.
     * @param source SPL token wallet funding this transaction
     * @param destination Destined SPL token wallet
     * @param amount 64 bit amount of tokens to send
     * @param owner account/private key signing this transaction
     * @return transaction id for explorer
     */
    public static SolanaTransactionInstruction transfer(
            SolanaPublicKey source,
            SolanaPublicKey destination,
            long amount,
            SolanaPublicKey owner) {

        return transfer(source, destination, amount, owner, null);
    }
    
    /**
     * 
     *
     * @param source 
     * @param destination 
     * @param amount 
     * @param owner 
     * @param reference 
     * @return 
     */
    public static SolanaTransactionInstruction transfer(
            SolanaPublicKey source,
            SolanaPublicKey destination,
            long amount,
            SolanaPublicKey owner,
            SolanaPublicKey reference) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(source, false, true));
        keys.add(new AccountMeta(destination, false, true));
        keys.add(new AccountMeta(owner, true, false));
        if (reference != null)
            keys.add(new AccountMeta(reference, false, false));

        byte[] transactionData = encodeTransferTokenInstructionData(amount);

        return new SolanaTransactionInstruction(PROGRAM_ID, keys, transactionData);
    }

    /**
     * 
     *
     * @param source 
     * @param destination 
     * @param amount 
     * @param decimals 
     * @param owner 
     * @param tokenMint 
     * @param reference 
     * @return 
     */
    public static SolanaTransactionInstruction transferChecked(
            SolanaPublicKey source,
            SolanaPublicKey destination,
            long amount,
            byte decimals,
            SolanaPublicKey owner,
            SolanaPublicKey tokenMint,
            SolanaPublicKey reference) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(source, false, true));
        // index 1 = token mint (https://docs.rs/spl-token/3.1.0/spl_token/instruction/enum.TokenInstruction.html#variant.TransferChecked)
        keys.add(new AccountMeta(tokenMint, false, false));
        keys.add(new AccountMeta(destination, false, true));
        keys.add(new AccountMeta(owner, true, false));

        if (reference != null)
            keys.add(new AccountMeta(reference, false, false));

        byte[] transactionData = encodeTransferCheckedTokenInstructionData(amount, decimals);

        return new SolanaTransactionInstruction(PROGRAM_ID, keys, transactionData);
    }

    /**
     * 
     *
     * @param owner 
     * @param mint 
     * @return 
     */
    public static SolanaPublicKey findProgramAddress(final SolanaPublicKey owner, final SolanaPublicKey mint) {

        List<byte[]> seeds = new ArrayList<>();
        seeds.add(owner.toByteArray());
        seeds.add(TokenProgram.PROGRAM_ID.toByteArray());
        seeds.add(mint.toByteArray());

        return SolanaPublicKey.findProgramAddress(seeds, TokenProgram.ASSOCIATED_TOKEN_PROGRAM_ID).getAddress();
    }

    /**
     * 
     *
     * @param payer 
     * @param mint 
     * @param owner 
     * @return 
     */
    public static SolanaTransactionInstruction createAccount(
            final SolanaPublicKey payer,
            final SolanaPublicKey mint,
            final SolanaPublicKey owner) {

        List<byte[]> seeds = new ArrayList<>();
        seeds.add(owner.toByteArray());
        seeds.add(TokenProgram.PROGRAM_ID.toByteArray());
        seeds.add(mint.toByteArray());

        SolanaPublicKey associatedToken =
                SolanaPublicKey.findProgramAddress(seeds, TokenProgram.ASSOCIATED_TOKEN_PROGRAM_ID).getAddress();

        return createAccount(associatedToken, payer, mint, owner);
    }

    /**
     * 
     *
     * @param associatedToken 
     * @param payer 
     * @param mint 
     * @param owner 
     * @return 
     */
    public static SolanaTransactionInstruction createAccount(
            final SolanaPublicKey associatedToken,
            final SolanaPublicKey payer,
            final SolanaPublicKey mint,
            final SolanaPublicKey owner) {

        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(payer, true, true));
        keys.add(new AccountMeta(associatedToken, false, true));
        keys.add(new AccountMeta(owner, false, false));
        keys.add(new AccountMeta(mint, false, false));
        keys.add(new AccountMeta(SystemProgram.PROGRAM_ID, false, false));
        keys.add(new AccountMeta(TokenProgram.PROGRAM_ID, false, false));
        keys.add(new AccountMeta(SYSVAR_RENT_PUBKEY, false, false));

        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) CREATE_METHOD_ID);

        return new SolanaTransactionInstruction(ASSOCIATED_TOKEN_PROGRAM_ID, keys, buffer.array());
    }

    /**
     * 
     *
     * @param associatedToken 
     * @param mint 
     * @param owner 
     * @return 
     */
    public static SolanaTransactionInstruction initializeAccount(
            final SolanaPublicKey associatedToken,
            final SolanaPublicKey mint,
            final SolanaPublicKey owner) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(associatedToken, false, true));
        keys.add(new AccountMeta(mint, false, false));
        keys.add(new AccountMeta(owner, false, true));
        keys.add(new AccountMeta(SYSVAR_RENT_PUBKEY, false, false));

        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) INITIALIZE_METHOD_ID);

        return new SolanaTransactionInstruction(PROGRAM_ID, keys, buffer.array());
    }

    /**
     * 
     *
     * @param source 
     * @param destination 
     * @param owner 
     * @return 
     */
    public static SolanaTransactionInstruction closeAccount(
            final SolanaPublicKey source,
            final SolanaPublicKey destination,
            final SolanaPublicKey owner) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(source, false, true));
        keys.add(new AccountMeta(destination, false, true));
        keys.add(new AccountMeta(owner, true, false));

        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) CLOSE_ACCOUNT_METHOD_ID);

        return new SolanaTransactionInstruction(PROGRAM_ID, keys, buffer.array());
    }

    /**
     * 
     *
     * @param amount 
     * @return 
     */
    private static byte[] encodeTransferTokenInstructionData(long amount) {
        ByteBuffer result = ByteBuffer.allocate(9);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put((byte) TRANSFER_METHOD_ID);
        result.putLong(amount);

        return result.array();
    }

    /**
     * 
     *
     * @param amount 
     * @param decimals 
     * @return 
     */
    private static byte[] encodeTransferCheckedTokenInstructionData(long amount, byte decimals) {
        ByteBuffer result = ByteBuffer.allocate(10);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put((byte) TRANSFER_CHECKED_METHOD_ID);
        result.putLong(amount);
        result.put(decimals);

        return result.array();
    }

    /**
     * create instruction for init mint
     *
     * @param payer
     * @param mint
     * @param tokenDecimal
     * @param mintAuthority
     * @param freezeAuthority
     * @return
     */
    public static SolanaTransactionInstruction initializeMintInstructionData(SolanaAccount payer,
                                                                              SolanaAccount mint,
                                                                              long tokenDecimal,
                                                                              SolanaAccount mintAuthority,
                                                                              SolanaAccount freezeAuthority){
        //init mint
        List<AccountMeta> keys = new ArrayList<>();
        keys.add(new AccountMeta(mint.getPublicKey(), true, true));
        keys.add(new AccountMeta(TokenProgram.SYSVAR_RENT_PUBKEY, false, false));

        ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + 32 + 1 + (freezeAuthority != null ? 32 : 0));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        //init mint
        buffer.put((byte)0);
        buffer.put((byte)tokenDecimal);
        buffer.put(mintAuthority.getPublicKey().toByteArray());
        //has freeze_authority
        buffer.put((byte) (freezeAuthority != null ? 1 : 0));
        //freeze_authority
        if(freezeAuthority!=null) {
            buffer.put(freezeAuthority.getPublicKey().toByteArray());
        }
        return new SolanaTransactionInstruction(TokenProgram.PROGRAM_ID, keys, buffer.array());
    }

    /**
     * create instruction for init mint USE 2022 version
     *
     * @param payer
     * @param mint
     * @param tokenDecimal
     * @param mintAuthority
     * @param freezeAuthority
     * @return
     */
    public static SolanaTransactionInstruction initializeMintInstructionData2022(SolanaAccount payer,
                                                                             SolanaAccount mint,
                                                                             long tokenDecimal,
                                                                             SolanaAccount mintAuthority,
                                                                             SolanaAccount freezeAuthority){
        //init mint
        List<AccountMeta> keys = new ArrayList<>();
        keys.add(new AccountMeta(mint.getPublicKey(), true, true));
        keys.add(new AccountMeta(TokenProgram.SYSVAR_RENT_PUBKEY, false, false));

        ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + 32 + 1 + (freezeAuthority != null ? 32 : 0));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        //init mint
        buffer.put((byte)0);
        buffer.put((byte)tokenDecimal);
        buffer.put(mintAuthority.getPublicKey().toByteArray());
        //has freeze_authority
        buffer.put((byte) (freezeAuthority != null ? 1 : 0));
        //freeze_authority
        if(freezeAuthority!=null) {
            buffer.put(freezeAuthority.getPublicKey().toByteArray());
        }
        return new SolanaTransactionInstruction(TokenProgram.PROGRAM_ID_2022, keys, buffer.array());
    }

    /**
     * Create Transaction Of Create a Mint
     * 1、Create Mint Account
     * 2、Initialize Mint
     *
     * @param payer
     * @param mint
     * @param mintAuthority
     * @param freezeAuthority
     * @param space
     * @param lamports
     * @param tokenDecimal
     * @param latestBlockhash
     * @return
     */
    private static SolanaTransaction createMintTxInner(SolanaAccount payer,
                                    SolanaAccount mint,
                                    SolanaAccount mintAuthority,
                                    SolanaAccount freezeAuthority,
                                    long space,
                                    long lamports,
                                    long tokenDecimal,
                                    String latestBlockhash,
                                    SolanaPublicKey programId){
        SolanaTransaction transaction = new SolanaTransaction();
        //instruction for create a new mint account
        transaction.addInstruction(//
                SystemProgram.createAccount(
                        payer.getPublicKey(),
                        mint.getPublicKey(),
                        lamports,
                        space,
                        programId
                )//
        );
        //instruction for initialize mint
        transaction.addInstruction(
                TokenProgram.initializeMintInstructionData(
                        payer,
                        mint,
                        tokenDecimal,
                        mintAuthority,
                        freezeAuthority));
        //last block hash
        transaction.setRecentBlockHash(latestBlockhash);
        //fee payer
        transaction.setFeePayer(payer.getPublicKey());
        //signers
        List<SolanaAccount> signers = new ArrayList<>();
        signers.add(payer);
        signers.add(mint);
        transaction.sign(signers);
        return transaction;
    }

    /**
     *  by PROGRAM_ID
     *
     * @param payer
     * @param mint
     * @param mintAuthority
     * @param freezeAuthority
     * @param space
     * @param lamports
     * @param tokenDecimal
     * @param latestBlockhash
     * @return
     */
    public static SolanaTransaction createMintTx(SolanaAccount payer,
                                                      SolanaAccount mint,
                                                      SolanaAccount mintAuthority,
                                                      SolanaAccount freezeAuthority,
                                                      long space,
                                                      long lamports,
                                                      long tokenDecimal,
                                                      String latestBlockhash){
        return createMintTxInner(payer,mint,mintAuthority,freezeAuthority,space,lamports,tokenDecimal,latestBlockhash,PROGRAM_ID);
    }
    /**
     * Create Transaction Of Create a Mint
     * 1、Create Mint Account
     * 2、Initialize Mint
     *
     * @param payer
     * @param mint
     * @param mintAuthority
     * @param freezeAuthority
     * @param space
     * @param lamports
     * @param tokenDecimal
     * @param latestBlockhash
     * @return
     */
    public static SolanaTransaction createMintTx2022(SolanaAccount payer,
                                                 SolanaAccount mint,
                                                 SolanaAccount mintAuthority,
                                                 SolanaAccount freezeAuthority,
                                                 long space,
                                                 long lamports,
                                                 long tokenDecimal,
                                                 String latestBlockhash){
        return createMintTxInner(payer,mint,mintAuthority,freezeAuthority,space,lamports,tokenDecimal,latestBlockhash,PROGRAM_ID_2022);
    }
}